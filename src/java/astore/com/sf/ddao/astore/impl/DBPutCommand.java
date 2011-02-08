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

package com.sf.ddao.astore.impl;

import com.sf.ddao.conn.ConnectionHandlerHelper;
import com.sf.ddao.factory.param.ParameterHelper;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by psyrtsov
 */
public class DBPutCommand implements Serializable, Command {
    private static final long serialVersionUID = 4584656621930746876L;
    private String sql;
    private List<Object> paramData;

    public DBPutCommand() {
    }

    public DBPutCommand(String sql, List<Object> paramData) {
        this.sql = sql;
        this.paramData = paramData;
    }

    public boolean execute(Context context) throws Exception {
        PreparedStatement preparedStatement = prepareStatement(context);
        preparedStatement.executeUpdate();
        preparedStatement.close();
        return CONTINUE_PROCESSING;
    }

    public PreparedStatement prepareStatement(Context context) throws SQLException {
        final Connection connection = ConnectionHandlerHelper.getConnection(context);
        PreparedStatement preparedStatement;
        preparedStatement = connection.prepareStatement(sql);
        int i = 1;
        for (Object parameter : paramData) {
            ParameterHelper.bind(preparedStatement, i++, parameter, context);
        }
        return preparedStatement;
    }
}
