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
import com.sf.ddao.alinker.initializer.InitializerException;
import com.sf.ddao.alinker.inject.Inject;
import com.sf.ddao.chain.CtxHelper;
import com.sf.ddao.chain.MethodCallCtx;
import com.sf.ddao.factory.StatementFactory;
import com.sf.ddao.handler.Intializible;
import com.sf.ddao.orm.ResultSetMapper;
import com.sf.ddao.orm.ResultSetMapperRegistry;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Created by: Pavel Syrtsov
 * Date: Apr 2, 2007
 * Time: 8:10:29 PM
 */
public class SelectSqlOperation implements Command, Intializible {
    private StatementFactory statementFactory;
    private Method method;

    @Inject
    public SelectSqlOperation(StatementFactory statementFactory) {
        this.statementFactory = statementFactory;
    }


    public boolean execute(Context context) throws Exception {
        try {
            final MethodCallCtx callCtx = CtxHelper.get(context, MethodCallCtx.class);
            final Object[] args = callCtx.getArgs();
            PreparedStatement preparedStatement = statementFactory.createStatement(context, Integer.MAX_VALUE);
            ResultSet resultSet = preparedStatement.executeQuery();
            ResultSetMapper resultSetMapper = ResultSetMapperRegistry.getResultSetMapper(method, args, resultSet);
            while (resultSet.next()) {
                if (!resultSetMapper.addRecord(resultSet)) {
                    break;
                }
            }
            resultSet.close();
            preparedStatement.close();
            final Object res = resultSetMapper.getResult();
            callCtx.setLastReturn(res);
            return CONTINUE_PROCESSING;
        } catch (Exception t) {
            throw new DaoException("Failed to execute sql operation for " + method, t);
        }
    }

    public void init(AnnotatedElement element, String sql) throws InitializerException {
        try {
            method = (Method) element;
            statementFactory.init(element, sql);
        } catch (Exception e) {
            throw new InitializerException("Failed to initialize sql operation for " + element, e);
        }
    }

    public void init(AnnotatedElement element, Annotation annotation) throws InitializerException {
        Select selectSql = element.getAnnotation(Select.class);
        init(element, selectSql.value());
    }
}
