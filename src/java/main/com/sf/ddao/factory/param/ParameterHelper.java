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
import java.math.BigInteger;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

/**
 * Created-By: Pavel Syrtsov
 * Date: Apr 10, 2008
 * Time: 4:30:20 PM
 */
public abstract class ParameterHelper implements ParameterHandler {
    public static final Logger log = LoggerFactory.getLogger(ParameterHelper.class.getName());
    protected String name;
    private boolean ref;

    public void init(AnnotatedElement element, String name, boolean isRef) {
        this.name = name;
        this.ref = isRef;
    }

    public void appendParam(Context context, StringBuilder sb) throws ParameterException {
        if (ref) {
            sb.append("?");
            return;
        }
        Object param = extractParam(context);
        if (param == null) {
            throw new ParameterException("ParameterHandler '" + this.name + "' is not defined");
        }
        if (param instanceof StatementParamter) {
            StatementParamter statementParamter = (StatementParamter) param;
            statementParamter.appendParam(context, sb);
        } else {
            sb.append(param.toString());
        }
    }

    public int bindParam(PreparedStatement preparedStatement, int idx, Context context) throws ParameterException {
        Object param = extractParam(context);
        log.debug("query parameter {}={}", name, param);
        try {
            if (param instanceof StatementParamter) {
                StatementParamter statementParamter = (StatementParamter) param;
                int res = statementParamter.bindParam(preparedStatement, idx, context);
                assert res == 1;
            } else {
                bind(preparedStatement, idx, param);
            }
        } catch (Exception e) {
            throw new ParameterException("Failed to bindParam parameter " + name + " to index " + idx, e);
        }
        return 1;
    }

    public static void bind(PreparedStatement preparedStatement, int idx, Object param) throws SQLException, ParameterException {
        if (param == null) {
            final int parameterType = preparedStatement.getParameterMetaData().getParameterType(idx);
            preparedStatement.setNull(idx, parameterType);
            return;
        }
        Class<?> clazz = param.getClass();
        if (clazz == Integer.class || clazz == Integer.TYPE) {
            preparedStatement.setInt(idx, (Integer) param);
        } else if (clazz == String.class) {
            preparedStatement.setString(idx, (String) param);
        } else if (clazz == Long.class || clazz == Long.TYPE) {
            preparedStatement.setLong(idx, (Long) param);
        } else if (clazz == Boolean.class || clazz == Boolean.TYPE) {
            preparedStatement.setBoolean(idx, (Boolean) param);
        } else if (BigInteger.class.isAssignableFrom(clazz)) {
            preparedStatement.setString(idx, param.toString());
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
