/*
 * Copyright 2008 Pavel Syrtsov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and limitations
 *  under the License.
 */
package com.sf.ddao.conn;

import com.sf.ddao.chain.CtxHelper;
import org.apache.commons.chain.Context;
import org.apache.commons.chain.Filter;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * ConnectionHandlerHelper helps to deal with connection allocation and transactions handling
 * it keeps connection in context for autocommit mode and in thread local variable when using transactions.
 * <p/>
 * Created by: pavel
 * Date: Jun 11, 2008
 * Time: 10:50:45 PM
 */
public abstract class ConnectionHandlerHelper implements Filter {
    public static final String CONNECTION_KEY = ConnectionHandlerHelper.class.toString() + "_CONN";

    private final ThreadLocal<Connection> connectionOnHold = new ThreadLocal<Connection>();

    public boolean execute(Context context) throws Exception {
        CtxHelper.put(context, ConnectionHandlerHelper.class, this);
        return CONTINUE_PROCESSING;
    }

    public boolean postprocess(Context context, Exception exception) {
        closeConnection(context);
        return CONTINUE_PROCESSING;
    }

    public static Connection setConnection(Context context, Connection conn) {
        //noinspection unchecked
        return (Connection) context.put(CONNECTION_KEY, conn);
    }


    public static Connection getConnection(Context context) throws SQLException {
        Connection conn = (Connection) context.get(CONNECTION_KEY);
        if (conn == null) {
            ConnectionHandlerHelper connectionHandlerHelper = CtxHelper.get(context, ConnectionHandlerHelper.class);
            conn = connectionHandlerHelper.connectionOnHold.get();
            if (conn != null) {
                return conn;
            }
            conn = connectionHandlerHelper.createConnection(context);
            setConnection(context, conn);

        }
        return conn;
    }

    public static void closeConnection(Context context) {
        Connection conn = (Connection) context.remove(CONNECTION_KEY);
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void putConnectionOnHold(Context context) {
        ConnectionHandlerHelper connectionHandlerHelper = CtxHelper.get(context, ConnectionHandlerHelper.class);
        Connection conn = (Connection) context.remove(CONNECTION_KEY);
        connectionHandlerHelper.connectionOnHold.set(conn);
    }

    public static Connection getConnectionOnHold(Context context) {
        ConnectionHandlerHelper connectionHandlerHelper = CtxHelper.get(context, ConnectionHandlerHelper.class);
        return connectionHandlerHelper.connectionOnHold.get();
    }

    public static Connection releaseConnectionOnHold(Context context) {
        ConnectionHandlerHelper connectionHandlerHelper = CtxHelper.get(context, ConnectionHandlerHelper.class);
        Connection res = connectionHandlerHelper.connectionOnHold.get();
        connectionHandlerHelper.connectionOnHold.remove();
        return res;
    }

    public abstract Connection createConnection(Context chainInvocationContext) throws SQLException;
}
