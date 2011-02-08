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

package com.sf.ddao.astore.impl;

import com.sf.ddao.alinker.initializer.InitializerException;
import com.sf.ddao.alinker.inject.Link;
import com.sf.ddao.astore.AsyncDB;
import com.sf.ddao.astore.AsyncDBPut;
import com.sf.ddao.factory.StatementFactory;
import com.sf.ddao.factory.StatementFactoryException;
import com.sf.ddao.factory.param.DefaultParameter;
import com.sf.ddao.factory.param.ParameterHandler;
import com.sf.ddao.handler.Intializible;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by psyrtsov
 */
public class AsyncDBPutOperation implements Command, Intializible {
    private final StatementFactory statementFactory;
    private final ParameterHandler cacheKeyParam;
    private final ParameterHandler cacheValueParam;

    @Link
    public AsyncDBPutOperation(StatementFactory statementFactory, DefaultParameter cacheKeyParam, DefaultParameter cacheValueParam) {
        this.statementFactory = statementFactory;
        this.cacheKeyParam = cacheKeyParam;
        this.cacheValueParam = cacheValueParam;
    }

    public boolean execute(Context context) throws Exception {
        final String sql = statementFactory.createText(context);
        final List<Object> paramData = extractParamData(context);
        Command dbPut = new DBPutCommand(sql, paramData);
        Object key = cacheKeyParam.extractParam(context);
        Object value = cacheValueParam.extractParam(context);
        final AsyncDB asyncDB = AsyncStoreHandler.getAsyncDB(context);
        //noinspection unchecked
        asyncDB.put(key, value, dbPut);
        return CONTINUE_PROCESSING;
    }

    private List<Object> extractParamData(Context context) throws SQLException {
        final List<ParameterHandler> refParametersList = statementFactory.getRefParametersList();
        final List<Object> paramData = new ArrayList<Object>(refParametersList.size());
        for (ParameterHandler parameter : refParametersList) {
            Object o = parameter.extractParam(context);
            paramData.add(o);
        }
        return paramData;
    }

    public void init(AnnotatedElement element, Annotation annotation) throws InitializerException {
        AsyncDBPut asyncDBPut = (AsyncDBPut) annotation;
        final String sql = asyncDBPut.sql();
        final String cacheKey = asyncDBPut.cacheKey();
        final String cacheValue = asyncDBPut.cacheValue();
        try {
            cacheKeyParam.init(element, cacheKey, true);
            cacheValueParam.init(element, cacheValue, true);
            statementFactory.init(element, sql);
        } catch (StatementFactoryException e) {
            throw new InitializerException("Failed to setup sql operation " + sql + " for method " + element, e);
        }
    }
}
