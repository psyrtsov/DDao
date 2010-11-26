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

package com.sf.ddao.kvs.impl;

import com.sf.ddao.DaoException;
import com.sf.ddao.alinker.initializer.InitializerException;
import com.sf.ddao.alinker.inject.Link;
import com.sf.ddao.chain.CtxHelper;
import com.sf.ddao.chain.MethodCallCtx;
import com.sf.ddao.conn.ConnectionHandlerHelper;
import com.sf.ddao.factory.StatementFactory;
import com.sf.ddao.factory.StatementFactoryException;
import com.sf.ddao.factory.param.ThreadLocalParameter;
import com.sf.ddao.kvs.PutBean;
import org.apache.commons.chain.Context;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.sql.Connection;
import java.sql.PreparedStatement;

/**
 * Created by pavel
 * Date: Jul 22, 2009
 * Time: 6:57:37 PM
 */
public class PutBeanKVSOperation extends KVSOperationBase {
    public static final String ID_FIELD_NAME = "id";
    private StatementFactory statementFactory;
    private AnnotatedElement element;

    @Link
    public PutBeanKVSOperation(StatementFactory statementFactory) {
        this.statementFactory = statementFactory;
    }

    public boolean execute(Context context) throws Exception {
        try {
            final MethodCallCtx callCtx = CtxHelper.get(context, MethodCallCtx.class);
            Connection connection = ConnectionHandlerHelper.getConnection(context);
            final Object[] args = callCtx.getArgs();
            PreparedStatement preparedStatement = statementFactory.createStatement(context);
            preparedStatement.executeUpdate();
            preparedStatement.close();
            String key = keyFactory.createText(context);
            keyValueStore.set(key, args[0]);
            return CONTINUE_PROCESSING;
        } catch (Exception t) {
            throw new DaoException("Failed to execute sql operation for " + element, t);
        } finally {
            ThreadLocalParameter.remove(ID_FIELD_NAME);
        }
    }

    public void init(AnnotatedElement element, Annotation annotation) throws InitializerException {
        PutBean putBean = (PutBean) annotation;
        this.element = element;
        super.init(element, putBean.key());
        try {
            statementFactory.init(element, putBean.sql());
        } catch (StatementFactoryException e) {
            throw new InitializerException("Failed to initialize sql operation for " + element, e);
        }
    }

}
