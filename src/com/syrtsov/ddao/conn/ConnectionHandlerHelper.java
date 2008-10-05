/**
 * Copyright 2008 Pavel Syrtsov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations
 * under the License.
 */
package com.syrtsov.ddao.conn;

import com.syrtsov.alinker.initializer.InitializerException;
import com.syrtsov.alinker.inject.Inject;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by: pavel
 * Date: Jun 11, 2008
 * Time: 10:50:45 PM
 */
public abstract class ConnectionHandlerHelper implements InvocationHandler {
    private DaoInvocationHandler daoInvocationHandler;
    protected volatile ConnectionHolderHelper connectionHolderHelper = null;

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Connection connection = getConnection(method, args);
        return invoke(connection, method, args);
    }

    public Object invoke(Connection connection, Method method, Object[] args) throws Throwable {
        // psdo: we could generalize this by introducing meta tag for connection handler level
        TransactionStarter transactionStarter = method.getAnnotation(TransactionStarter.class);
        if (transactionStarter != null) {
            return putConnectionOnHold(connection);
        }
        try {
            return daoInvocationHandler.invoke(connection, method, args);
        } finally {
            closeIfNotOnHold(connection);
        }
    }

    public Connection getConnection(Method method, Object[] args) throws SQLException {
        if (connectionHolderHelper != null && connectionHolderHelper.hasConnectionOnHold()) {
            Connection res = connectionHolderHelper.getConnectionProxy();
            if (res != null) {
                return res;
            }
        }
        return createConnection(method, args);
    }

    public void closeIfNotOnHold(Connection connection) throws SQLException {
        if (connectionHolderHelper != null && connectionHolderHelper.hasConnectionOnHold()) {
            return;
        }
        connection.close();
    }

    public abstract Connection createConnection(Method method, Object[] args) throws SQLException;

    public void init(Class<?> iFace, Annotation annotation, List<Class<?>> iFaceList) throws InitializerException {
    }

    @Inject
    public void setDaoInvocationHandler(DaoInvocationHandler daoInvocationHandler) {
        this.daoInvocationHandler = daoInvocationHandler;
    }

    public Connection putConnectionOnHold(Connection connection) {
        synchronized (this) {
            if (connectionHolderHelper == null) {
                connectionHolderHelper = new ConnectionHolderHelper();
            }
        }
        connectionHolderHelper.putConnectionOnHold(connection);
        return connectionHolderHelper.getConnectionProxy();
    }
}
