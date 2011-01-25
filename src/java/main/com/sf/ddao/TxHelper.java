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

package com.sf.ddao;

import com.sf.ddao.conn.ConnectionHandlerHelper;
import org.apache.commons.chain.Context;

import java.sql.Connection;
import java.util.concurrent.Callable;

/**
 * TxHelper provides utility methods that can be used with DDao framework.
 * <p/>
 * Created by Pavel Syrtsov
 * Date: Aug 25, 2007
 * Time: 2:18:03 PM
 */
public class TxHelper {
    public static final ThreadLocal<Connection> connectionOnHold = new ThreadLocal<Connection>();

    public static <T, SK> T execInTx(TransactionableDao<SK> dao, Callable<T> callable, SK... shardKeys) throws Exception {
        Context context = dao.startTransaction(shardKeys == null || shardKeys.length == 0 ? null : shardKeys[0]);
        boolean success = false;
        Connection conn;
        try {
            conn = ConnectionHandlerHelper.getConnectionOnHold(context);
            connectionOnHold.set(conn);
            conn.setAutoCommit(false);
            T res = callable.call();
            conn.commit();
            success = true;
            return res;
        } finally {
            connectionOnHold.remove();
            conn = ConnectionHandlerHelper.releaseConnectionOnHold(context);
            if (!success) {
                conn.rollback();
            }
            conn.close();
        }
    }

    public static <SK> void execInTx(TransactionableDao dao, final Runnable runnable, SK... shardKeys) throws Exception {
        execInTx(dao, new Callable<Object>() {
            public Object call() throws Exception {
                runnable.run();
                return null;
            }
        }, shardKeys);
    }

    public static Connection getConnectionOnHold() {
        return connectionOnHold.get();
    }
}
