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
import com.sf.ddao.Update;
import com.sf.ddao.alinker.initializer.InitializerException;
import com.sf.ddao.alinker.inject.Inject;
import com.sf.ddao.chain.ChainInvocationContext;
import com.sf.ddao.chain.ChainMemberInvocationHandler;
import com.sf.ddao.conn.ConnectionHandlerHelper;
import com.sf.ddao.factory.StatementFactory;
import com.sf.ddao.factory.StatementFactoryException;
import com.sf.ddao.handler.Intializible;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;

/**
 * Created by: Pavel Syrtsov
 * Date: Apr 23, 2007
 * Time: 11:55:52 PM
 */
public class UpdateSqlOperation implements ChainMemberInvocationHandler, Intializible {
    private StatementFactory statementFactory;
    private Method method;

    @Inject
    public UpdateSqlOperation(StatementFactory statementFactory) {
        this.statementFactory = statementFactory;
    }

    public void init(AnnotatedElement element, String sql) throws InitializerException {
        method = (Method) element;
        try {
            statementFactory.init(element, sql);
        } catch (StatementFactoryException e) {
            throw new InitializerException("Failed to setup sql operation " + sql + " for method " + element, e);
        }
    }

    @Override
    public void init(AnnotatedElement element, Annotation annotation) throws InitializerException {
        Update updateAnnotation = (Update) annotation;
        init(element, updateAnnotation.value());
    }

    @Override
    public Object invoke(ChainInvocationContext context, boolean hasNext) throws Throwable {
        try {
            final Connection connection = ConnectionHandlerHelper.getConnection(context);
            PreparedStatement preparedStatement = statementFactory.createStatement(connection, context.getArgs(), Integer.MAX_VALUE);
            int res = preparedStatement.executeUpdate();
            preparedStatement.close();
            if (method.getReturnType() == Integer.TYPE || method.getReturnType() == Integer.class) {
                return res;
            }
            return null;
        } catch (Exception t) {
            throw new DaoException("Failed to execute sql operation for " + method, t);
        }
    }

    public Method getMethod() {
        return method;
    }

    public StatementFactory getStatementFactory() {
        return statementFactory;
    }
}
