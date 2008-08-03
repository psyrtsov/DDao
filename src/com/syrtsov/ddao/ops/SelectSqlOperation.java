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

package com.syrtsov.ddao.ops;

import com.syrtsov.alinker.initializer.InitializerException;
import com.syrtsov.ddao.DaoException;
import com.syrtsov.ddao.Select;
import com.syrtsov.ddao.SelectCallback;
import com.syrtsov.ddao.SqlOperation;
import com.syrtsov.ddao.factory.StatementFactory;
import com.syrtsov.ddao.factory.StatementFactoryException;
import com.syrtsov.ddao.factory.StatementFactoryManager;
import com.syrtsov.ddao.mapper.ResultSetMapper;
import com.syrtsov.ddao.mapper.ResultSetMapperException;
import com.syrtsov.ddao.mapper.ResultSetMapperRegistry;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Created by: Pavel Syrtsov
 * Date: Apr 2, 2007
 * Time: 8:10:29 PM
 */
public class SelectSqlOperation implements SqlOperation {
    private StatementFactory statementFactory;

    public Object invoke(Connection connection, Method method, Object[] args) {
        try {
            PreparedStatement preparedStatement = statementFactory.createStatement(connection, args);

            ResultSetMapper resultSetMapper;
            if (method.getReturnType() == Void.TYPE) {
                resultSetMapper = createCallbackMapper(args);
            } else {
                resultSetMapper = ResultSetMapperRegistry.getResultSetMapper(method);
            }
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                if (!resultSetMapper.addRecord(resultSet)) {
                    break;
                }
            }
            resultSet.close();
            preparedStatement.close();
            return resultSetMapper.getResult();
        } catch (Exception t) {
            throw new DaoException("Failed to execute sql operation for " + method, t);
        }
    }

    public void init(Method method) throws InitializerException {
        Select selectSql = method.getAnnotation(Select.class);
        init(method, selectSql.value());
    }

    public void init(Method method, String sql) throws InitializerException {
        try {
            statementFactory = StatementFactoryManager.createStatementFactory(method, sql);
        } catch (StatementFactoryException e) {
            throw new InitializerException("Failed to initialize sql operation for " + method, e);
        }
    }

    private ResultSetMapper createCallbackMapper(Object[] args) throws ResultSetMapperException {
        for (Object arg : args) {
            if (arg instanceof SelectCallback) {
                final SelectCallback selectCallback = (SelectCallback) arg;
                Type[] ifaces = selectCallback.getClass().getGenericInterfaces();
                for (Type type : ifaces) {
                    if (type instanceof ParameterizedType && type.toString().startsWith(SelectCallback.class.getName())) {
                        Type[] actualTypeArguments = ((ParameterizedType) type).getActualTypeArguments();
                        Type itemType = actualTypeArguments[0];
                        final ResultSetMapper resultSetMapper = ResultSetMapperRegistry.getResultMapper(itemType);
                        return new ResultSetMapper() {
                            public boolean addRecord(ResultSet resultSet) throws Exception {
                                resultSetMapper.addRecord(resultSet);
                                Object result = resultSetMapper.getResult();
                                //noinspection unchecked
                                return selectCallback.processRecord(result);
                            }

                            public Object getResult() {
                                return null;
                            }
                        };
                    }
                }
            }
        }
        throw new ResultSetMapperException("Method with void return type has to have argument of type "
                + SelectCallback.class);
    }

}
