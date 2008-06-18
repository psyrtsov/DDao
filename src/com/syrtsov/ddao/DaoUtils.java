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

package com.syrtsov.ddao;

import java.sql.Connection;
import java.util.concurrent.Callable;

/**
 * DaoUtils provides utility methods that can be used with DDao framework.
 * <p/>
 * Created by Pavel Syrtsov
 * Date: Aug 25, 2007
 * Time: 2:18:03 PM
 */
public class DaoUtils {
    public static <T> T execInTx(Object dao, Callable<T> callable) throws Exception {
        Connection conn = (Connection) dao;
        boolean success = false;
        try {
            conn.setAutoCommit(false);
            T res = callable.call();
            conn.commit();
            success = true;
            return res;
        } finally {
            if (!success) {
                conn.rollback();
            }
            conn.close();
        }
    }

    public static void execInTx(Object dao, final Runnable runnable) throws Exception {
        execInTx(dao, new Callable<Object>() {
            public Object call() throws Exception {
                runnable.run();
                return null;
            }
        });
    }
}
