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

package com.sf.ddao.factory.param;

import com.sf.ddao.factory.StatementParamter;
import org.apache.commons.chain.Context;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Date: Oct 27, 2009
 * Time: 3:36:12 PM
 */
public class ForwardParameter extends DefaultParameter {
    public static final String FUNC_NAME = "fwd";

    @Override
    public void appendParam(Context context, StringBuilder sb) throws SQLException {
        Object param = extractParam(context);
        if (param == null) {
            throw new SQLException("ParameterHandler '" + this.name + "' is not defined");
        }
        StatementParamter statementParamter = (StatementParamter) param;
        statementParamter.appendParam(context, sb);
    }

    @Override
    public int bindParam(PreparedStatement preparedStatement, int idx, Context context) throws SQLException {
        Object param = extractParam(context);
        if (param == null) {
            throw new SQLException("ParameterHandler '" + this.name + "' is not defined");
        }
        StatementParamter statementParamter = (StatementParamter) param;
        return statementParamter.bindParam(preparedStatement, idx, context);
    }
}
