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

package com.sf.ddao.ops;

import com.sf.ddao.DaoException;
import com.sf.ddao.Select;
import com.sf.ddao.SqlOperation;
import com.sf.ddao.alinker.initializer.InitializerException;
import com.sf.ddao.factory.StatementFactory;
import com.sf.ddao.factory.StatementFactoryException;
import com.sf.ddao.factory.StatementFactoryManager;
import com.sf.ddao.orm.ResultSetMapper;
import com.sf.ddao.orm.ResultSetMapperRegistry;

import java.lang.reflect.Method;
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
            PreparedStatement preparedStatement = statementFactory.createStatement(connection, args, Integer.MAX_VALUE);
            ResultSet resultSet = preparedStatement.executeQuery();
            ResultSetMapper resultSetMapper = ResultSetMapperRegistry.getResultSetMapper(method, args, resultSet);
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
}
