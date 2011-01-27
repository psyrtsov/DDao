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

import com.sf.ddao.DaoException;
import com.sf.ddao.astore.DBBatchGet;
import com.sf.ddao.chain.CtxHelper;
import com.sf.ddao.chain.MethodCallCtx;
import com.sf.ddao.factory.param.JoinListParameter;
import com.sf.ddao.orm.RSMapper;
import org.apache.commons.chain.Context;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Collection;
import java.util.Map;

/**
 * Created by psyrtsov
 */
public class DBBatchGetImpl implements DBBatchGet {
    public static final String KEY_LIST_CONTEXT_VALUE = "keyList";
    protected Context context;
    protected AsyncDBBatchGetOperation operation;

    public void init(AsyncDBBatchGetOperation operation, Context context) {
        this.operation = operation;
        this.context = context;
    }

    public Map batchGet(Collection keys) {
        try {
            final MethodCallCtx callCtx = CtxHelper.get(context, MethodCallCtx.class);
            //noinspection unchecked
            context.put(KEY_LIST_CONTEXT_VALUE, JoinListParameter.join(keys));
            PreparedStatement preparedStatement = operation.getStatementFactory().createStatement(context, Integer.MAX_VALUE);
            ResultSet resultSet = preparedStatement.executeQuery();
            RSMapper RSMapper = operation.getMapORMapperFactory().getInstance(callCtx.getArgs(), resultSet);
            Map map = (Map) RSMapper.handle(context, resultSet);
            resultSet.close();
            preparedStatement.close();
            return map;
        } catch (Exception t) {
            throw new DaoException("Failed to execute sql operation for " + operation.getMethod(), t);
        }
    }
}
