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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.AnnotatedElement;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

/**
 * Created-By: Pavel Syrtsov
 * Date: Apr 10, 2008
 * Time: 4:30:20 PM
 */
public abstract class ParameterHelper implements Parameter {
    public static final Logger log = LoggerFactory.getLogger(ParameterHelper.class.getName());
    protected String name;

    public void init(AnnotatedElement element, String name) {
        // psdo: move dealing with type to init method to make runtime faster
        this.name = name;
    }

    public String extractParam(Context context) throws ParameterException {
        Object param = extractData(context);
        if (param == null) {
            throw new ParameterException("Parameter '" + this.name + "' is not defined");
        }
        if (param instanceof StatementParamter) {
            StatementParamter statementParamter = (StatementParamter) param;
            return statementParamter.statementParameter();
        }
        return param.toString();
    }

    public void bind(PreparedStatement preparedStatement, int idx, Context context) throws ParameterException {
        Object param = extractData(context);
        log.debug("query parameter {}={}", name, param);
        try {
            if (param == null) {
                final int parameterType = preparedStatement.getParameterMetaData().getParameterType(idx);
                preparedStatement.setNull(idx, parameterType);
                return;
            }
            if (param instanceof StatementParamter) {
                StatementParamter statementParamter = (StatementParamter) param;
                statementParamter.bind(preparedStatement, idx, context);
            } else {
                bind(preparedStatement, idx, param);
            }
        } catch (Exception e) {
            throw new ParameterException("Failed to bind parameter " + name + " to index " + idx, e);
        }
    }

    public static void bind(PreparedStatement preparedStatement, int idx, Object param) throws SQLException, ParameterException {
        Class<?> clazz = param.getClass();
        if (clazz == Integer.class || clazz == Integer.TYPE) {
            preparedStatement.setInt(idx, (Integer) param);
        } else if (clazz == String.class) {
            preparedStatement.setString(idx, (String) param);
        } else if (clazz == Long.class) {
            preparedStatement.setLong(idx, (Long) param);
        } else if (Date.class.isAssignableFrom(clazz)) {
            if (!java.sql.Date.class.isAssignableFrom(clazz)) {
                param = new java.sql.Date(((Date) param).getTime());
            }
            preparedStatement.setDate(idx, (java.sql.Date) param);
        } else if (Timestamp.class.isAssignableFrom(clazz)) {
            preparedStatement.setTimestamp(idx, (Timestamp) param);
        } else {
            throw new ParameterException("Unimplemented type mapping for " + clazz);
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" + name + "}";
    }
}
