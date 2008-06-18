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

package com.syrtsov.ddao.factory;

import com.syrtsov.ddao.DaoException;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.MessageFormat;

/**
 * MessageFormatStatementFactory uses {@link java.text.MessageFormat} to create statement's query text.
 * It will pass methods arguments to MessageFormat.format method call to create query.
 * This factory doesn't support parameter binding for statements.
 * <p/>
 * Created by Pavel Syrtsov
 * Date: Nov 3, 2007
 * Time: 8:17:20 PM
 */
public class MessageFormatStatementFactory implements StatementFactory {
    private String sql;
    private Method method;

    public void init(String sql, Method method) {
        this.sql = sql;
        this.method = method;
    }

    public PreparedStatement createStatement(Connection connection, Object[] args) throws DaoException {
        MessageFormat mf = new MessageFormat(sql);
        sql = mf.format(args);
        try {
            return connection.prepareStatement(sql);
        } catch (SQLException e) {
            throw new DaoException("Failed to create statement " + sql + " for method " + method);
        }
    }
}
