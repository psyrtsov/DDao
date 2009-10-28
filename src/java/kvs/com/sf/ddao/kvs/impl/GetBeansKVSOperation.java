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

package com.sf.ddao.kvs.impl;

import com.sf.ddao.DaoException;
import com.sf.ddao.alinker.initializer.InitializerException;
import com.sf.ddao.alinker.inject.Inject;
import com.sf.ddao.chain.CtxHelper;
import com.sf.ddao.chain.MethodCallCtx;
import com.sf.ddao.conn.ConnectionHandlerHelper;
import com.sf.ddao.factory.StatementFactory;
import com.sf.ddao.factory.StatementFactoryException;
import com.sf.ddao.kvs.GetBeans;
import org.apache.commons.chain.Context;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * This SQL operations implements idea of storing
 * Created by: Pavel Syrtsov
 * Date: Apr 19, 2009
 * Time: 7:58:00 PM
 */
public class GetBeansKVSOperation extends KVSOperationBase {
    private StatementFactory statementFactory;
    private AnnotatedElement element;

    @Inject
    public GetBeansKVSOperation(StatementFactory statementFactory) {
        this.statementFactory = statementFactory;
    }

    public boolean execute(Context context) throws Exception {
        try {
            final MethodCallCtx callCtx = CtxHelper.get(context, MethodCallCtx.class);
            Connection connection = ConnectionHandlerHelper.getConnection(context);
            final Object[] args = callCtx.getArgs();
            PreparedStatement preparedStatement = statementFactory.createStatement(context);
            ResultSet resultSet = preparedStatement.executeQuery();
            List<String> keyList = new ArrayList<String>();
            while (resultSet.next()) {
                final String key = resultSet.getString(1);
                keyList.add(key);
            }
            resultSet.close();
            preparedStatement.close();
            List<Object> res = keyValueStore.getMulti(keyList);
            callCtx.setLastReturn(res);
            return CONTINUE_PROCESSING;
        } catch (Exception t) {
            throw new DaoException("Failed to execute sql operation for " + element, t);
        }
    }

    public void init(AnnotatedElement element, Annotation annotation) throws InitializerException {
        this.element = element;
        GetBeans getBeans = (GetBeans) annotation;
        super.init(element, (String) null);
        try {
            statementFactory.init(element, getBeans.sql());
        } catch (StatementFactoryException e) {
            throw new InitializerException("Failed to initialize sql operation for " + element, e);
        }
    }
}
