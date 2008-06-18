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

package com.syrtsov.ddao.factory.param;

import java.lang.reflect.Method;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.sql.Types;

/**
 * Created-By: Pavel Syrtsov
 * Date: Apr 10, 2008
 * Time: 4:30:20 PM
 */
public abstract class StatementParameterHelper implements StatementParameter {
    protected String name;

    public void init(Method method, String name) {
        this.name = name;
    }

    public abstract Object extractData(Object[] args) throws StatementParameterException;

    public String extractParam(Object[] args) throws StatementParameterException {
        Object data = extractData(args);
        return data.toString();
    }

    public void bind(PreparedStatement preparedStatement, int idx, Object[] args) throws StatementParameterException {
        Object param = extractData(args);
        try {
            if (param == null) {
                preparedStatement.setNull(idx, Types.NULL);
                return;
            }
            Class<?> clazz = param.getClass();
            if (clazz == Integer.class || clazz == Integer.TYPE) {
                preparedStatement.setInt(idx, (Integer) param);
            } else if (clazz == String.class) {
                preparedStatement.setString(idx, (String) param);
            } else if (Date.class.isAssignableFrom(clazz)) {
                preparedStatement.setDate(idx, (Date) param);
            } else if (Timestamp.class.isAssignableFrom(clazz)) {
                preparedStatement.setTimestamp(idx, (Timestamp) param);
            } else {
                throw new StatementParameterException("Unimplemented type mapping for " + clazz);
            }
        } catch (Exception e) {
            throw new StatementParameterException("Failed to bind parameter " + name + " to index " + idx, e);
        }
    }
}
