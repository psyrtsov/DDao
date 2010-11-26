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
import com.sf.ddao.astore.AsyncDBGet;
import com.sf.ddao.chain.CtxHelper;
import com.sf.ddao.chain.MethodCallCtx;
import com.sf.ddao.factory.StatementFactory;
import com.sf.ddao.factory.param.DefaultParameter;
import com.sf.ddao.factory.param.Parameter;
import com.sf.ddao.handler.Intializible;
import com.sf.ddao.ops.SelectSqlOperation;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.concurrent.Callable;

/**
 * Created by psyrtsov
 */
public class AsyncDBGetOperation implements Command, Intializible {
    private final SelectSqlOperation selectSqlOperation;
    private final Parameter cacheKeyParam;

    @Link
    public AsyncDBGetOperation(StatementFactory statementFactory, DefaultParameter cacheKeyParam) {
        this.cacheKeyParam = cacheKeyParam;
        this.selectSqlOperation = new SelectSqlOperation(statementFactory);
    }

    public boolean execute(Context context) throws Exception {
        final MethodCallCtx callCtx = CtxHelper.get(context, MethodCallCtx.class);
        Object key = cacheKeyParam.extractData(context);
        final AsyncDB asyncDB = AsyncStoreHandler.getAsyncDB(context);
        Callable dbGet = new DBGetCallable(context);
        @SuppressWarnings({"unchecked"})
        Object res = asyncDB.get(key, dbGet);
        callCtx.setLastReturn(res);
        return CONTINUE_PROCESSING;
    }

    public void init(AnnotatedElement element, Annotation annotation) throws InitializerException {
        AsyncDBGet asyncDBGet = (AsyncDBGet) annotation;
        final String sql = asyncDBGet.sql();
        final String cacheKey = asyncDBGet.cacheKey();
        cacheKeyParam.init(element, cacheKey);
        selectSqlOperation.init(element, sql);
    }

    private class DBGetCallable implements Callable {
        private final Context context;

        public DBGetCallable(Context context) {
            this.context = context;
        }

        public Object call() throws Exception {
            selectSqlOperation.execute(context);
            final MethodCallCtx callCtx = CtxHelper.get(context, MethodCallCtx.class);
            return callCtx.getLastReturn();
        }
    }
}
