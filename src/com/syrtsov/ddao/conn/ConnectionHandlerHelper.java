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
    private static final ThreadLocal<Connection> connectionOnHold = new ThreadLocal<Connection>();
    private DaoInvocationHandler daoInvocationHandler;

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Class<?> declaringClass = method.getDeclaringClass();
        if (declaringClass == Connection.class) {
            Connection connection;
            if ("close".equals(method.getName())) {
                connection = releaseConnectionOnHold();
                if (connection == null) {
                    return null;
                }
            } else {
                connection = getConnection(true);
            }
            return method.invoke(connection, args);
        }
        Connection connection = getConnection(false);
        try {
            return daoInvocationHandler.invoke(connection, method, args);
        } finally {
            closeIfNotOnHold(connection);
        }
    }

    private Connection releaseConnectionOnHold() {
        Connection res = connectionOnHold.get();
        if (res != null) {
            connectionOnHold.remove();
        }
        return res;
    }

    private void closeIfNotOnHold(Connection connection) throws SQLException {
        if (connectionOnHold.get() != connection) {
            connection.close();
        }
    }

    private Connection getConnection(boolean putOnHold) throws SQLException {
        Connection res = connectionOnHold.get();
        if (res != null) {
            return res;
        }
        res = createConnection();
        if (putOnHold) {
            connectionOnHold.set(res);
        }
        return res;
    }

    protected abstract Connection createConnection() throws SQLException;

    public void init(Class<?> iFace, Annotation annotation, List<Class<?>> iFaceList) throws InitializerException {
        iFaceList.add(Connection.class);
    }

    @Inject
    public void setDaoInvocationHandler(DaoInvocationHandler daoInvocationHandler) {
        this.daoInvocationHandler = daoInvocationHandler;
    }

    public DaoInvocationHandler getDaoInvocationHandler() {
        return daoInvocationHandler;
    }
}
