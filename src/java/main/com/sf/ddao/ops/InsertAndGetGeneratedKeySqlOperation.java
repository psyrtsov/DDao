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

package com.sf.ddao.ops;

import com.sf.ddao.DaoException;
import com.sf.ddao.InsertAndGetGeneratedKey;
import com.sf.ddao.chain.CtxHelper;
import com.sf.ddao.chain.InitializerException;
import com.sf.ddao.chain.Intializible;
import com.sf.ddao.chain.MethodCallCtx;
import com.sf.ddao.factory.StatementFactory;
import com.sf.ddao.factory.StatementFactoryException;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import javax.inject.Inject;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Created-By: Pavel Syrtsov
 * Date: Aug 11, 2007
 * Time: 2:47:16 PM
 */
public class InsertAndGetGeneratedKeySqlOperation implements Command, Intializible {
    @Inject
    private StatementFactory statementFactory;
    private Method method;

    public boolean execute(Context context) throws Exception {
        try {
            final MethodCallCtx callCtx = CtxHelper.get(context, MethodCallCtx.class);
            PreparedStatement preparedStatement = statementFactory.createStatement(context, true);
            Object res = null;
            preparedStatement.executeUpdate();
            ResultSet resultSet = preparedStatement.getGeneratedKeys();
            if (resultSet.next()) {
                if (method.getReturnType() == Integer.TYPE || method.getReturnType() == Integer.class) {
                    res = resultSet.getInt(1);
                } else if (method.getReturnType() == Long.TYPE || method.getReturnType() == Long.class) {
                    res = resultSet.getLong(1);
                } else if (method.getReturnType() == BigDecimal.class) {
                    res = resultSet.getBigDecimal(1);
                }
            }
            resultSet.close();
            preparedStatement.close();
            callCtx.setLastReturn(res);
            return CONTINUE_PROCESSING;
        } catch (Exception t) {
            throw new DaoException("Failed to execute sql operation for " + method, t);
        }
    }

    public void init(AnnotatedElement element, Annotation annotation) {
        method = (Method) element;
        InsertAndGetGeneratedKey insertAndGetGeneratedKey = element.getAnnotation(InsertAndGetGeneratedKey.class);
        String sql = insertAndGetGeneratedKey.value();
        try {
            statementFactory.init(element, sql);
        } catch (StatementFactoryException e) {
            throw new InitializerException("Failed to setup sql operation " + sql + " for method " + method, e);
        }
    }
}

