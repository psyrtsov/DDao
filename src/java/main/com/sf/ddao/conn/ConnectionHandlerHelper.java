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
package com.sf.ddao.conn;

import com.sf.ddao.alinker.initializer.InitializerException;
import com.sf.ddao.chain.ChainInvocationContext;
import com.sf.ddao.chain.ChainInvocationPostProcessor;
import com.sf.ddao.chain.ChainMemberInvocationHandler;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by: pavel
 * Date: Jun 11, 2008
 * Time: 10:50:45 PM
 */
public abstract class ConnectionHandlerHelper implements ChainMemberInvocationHandler, ChainInvocationPostProcessor {
    public static final ThreadLocal<Connection> connectionOnHold = new ThreadLocal<Connection>();

    public static final String CONNECTION_KEY = ConnectionHandlerHelper.class.toString() + "_CONN";

    public void init(AnnotatedElement element, Annotation annotation) throws InitializerException {
        // do nothing for now
    }

    /**
     * @param context - invocation context
     * @param hasNext - if false then this is last invocation on the chain
     *                ans result will be used as return value for whole the method call
     * @return value will be stored in lastReturn property of context
     */
    public Object invoke(ChainInvocationContext context, boolean hasNext) throws Throwable {
        context.put(ConnectionHandlerHelper.class.toString(), this);
        return null;
    }

    public void chainPostProcess(ChainInvocationContext context) throws Throwable {
        Connection conn = (Connection) context.get(CONNECTION_KEY);
        if (conn != null) {
            conn.close();
        }
    }


    public static Connection getConnection(ChainInvocationContext context) throws SQLException {
        Connection conn = (Connection) context.get(CONNECTION_KEY);
        if (conn == null) {
            conn = connectionOnHold.get();
            if (conn == null) {
                ConnectionHandlerHelper connectionHandlerHelper = (ConnectionHandlerHelper) context.get(ConnectionHandlerHelper.class.toString());
                conn = connectionHandlerHelper.createConnection(context);
            }
            context.put(CONNECTION_KEY, conn);
        }
        return conn;
    }

    public static void putConnectionOnHold(Connection connection) {
        connectionOnHold.set(connection);
    }

    public static Connection getConnectionOnHold() {
        return connectionOnHold.get();
    }

    public static Connection releaseConnectionOnHold() {
        Connection res = connectionOnHold.get();
        connectionOnHold.remove();
        return res;
    }

    public abstract Connection createConnection(ChainInvocationContext chainInvocationContext) throws SQLException;
}
