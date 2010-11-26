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
import com.sf.ddao.Update;
import com.sf.ddao.alinker.initializer.InitializerException;
import com.sf.ddao.alinker.inject.Link;
import com.sf.ddao.chain.CtxHelper;
import com.sf.ddao.chain.MethodCallCtx;
import com.sf.ddao.factory.StatementFactory;
import com.sf.ddao.factory.StatementFactoryException;
import com.sf.ddao.handler.Intializible;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.sql.PreparedStatement;

/**
 * Created by: Pavel Syrtsov
 * Date: Apr 23, 2007
 * Time: 11:55:52 PM
 */
public class UpdateSqlOperation implements Command, Intializible {
    private StatementFactory statementFactory;
    private Method method;

    @Link
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

    public void init(AnnotatedElement element, Annotation annotation) throws InitializerException {
        Update updateAnnotation = element.getAnnotation(Update.class);
        init(element, updateAnnotation.value());
    }

    public boolean execute(Context context) throws Exception {
        try {
            final MethodCallCtx callCtx = CtxHelper.get(context, MethodCallCtx.class);
            PreparedStatement preparedStatement = statementFactory.createStatement(context, Integer.MAX_VALUE);
            int res = preparedStatement.executeUpdate();
            preparedStatement.close();
            if (method.getReturnType() == Integer.TYPE || method.getReturnType() == Integer.class) {
                callCtx.setLastReturn(res);
            }
            return CONTINUE_PROCESSING;
        } catch (Exception t) {
            throw new DaoException("Failed to execute sql operation for " + method, t);
        }
    }
}
