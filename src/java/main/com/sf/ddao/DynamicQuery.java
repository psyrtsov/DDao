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

import com.sf.ddao.factory.StatementParamter;
import com.sf.ddao.factory.param.ParameterHelper;
import org.apache.commons.chain.Context;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by psyrtsov
 */
public class DynamicQuery implements StatementParamter {
    private StringBuilder sb = new StringBuilder();
    private List<Object> params = new ArrayList<Object>();

    public void appendParam(Context context, StringBuilder sb) throws SQLException {
        sb.append(this.sb.toString());
    }

    public int bindParam(PreparedStatement preparedStatement, int idx, Context context) throws SQLException {
        for (Object param : params) {
            try {
                ParameterHelper.bind(preparedStatement, idx++, param, context);
            } catch (SQLException e) {
                throw new SQLException("Paramter #" + --idx + ":" + param, e);
            }
        }
        return params.size();
    }

    public DynamicQuery add(String sql, Object... params) {
        sb.append(sql);
        this.params.addAll(Arrays.asList(params));
        return this;
    }

    public StringBuilder getSql() {
        return sb;
    }

    public List<Object> getParams() {
        return params;
    }

    @Override
    public String toString() {
        return "DynamicQuery{" +
                "sql='" + sb +
                ", params='" + params +
                "'}";
    }
}
