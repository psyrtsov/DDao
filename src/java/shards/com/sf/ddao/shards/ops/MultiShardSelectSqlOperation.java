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

package com.sf.ddao.shards.ops;

import com.google.inject.Injector;
import com.sf.ddao.chain.CtxHelper;
import com.sf.ddao.chain.MethodCallCtx;
import com.sf.ddao.conn.ConnectionHandlerHelper;
import com.sf.ddao.factory.param.JoinListParameter;
import com.sf.ddao.ops.SelectSqlOperation;
import com.sf.ddao.shards.MultiShardResultMerger;
import com.sf.ddao.shards.MultiShardSelect;
import com.sf.ddao.shards.conn.ShardedConnectionHandler;
import org.apache.commons.chain.Context;

import javax.inject.Inject;
import javax.sql.DataSource;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Created by psyrtsov
 */
public class MultiShardSelectSqlOperation extends SelectSqlOperation {
    public static final String KEY_LIST_CONTEXT_VALUE = "keyList";
    @Inject
    private Injector injector;
    private MultiShardResultMerger resultMerger;

    @Override
    public boolean execute(Context context) throws Exception {
        ShardedConnectionHandler shardedConnectionHandler = CtxHelper.get(context, ShardedConnectionHandler.class);
        Map<DataSource, Collection<Object>> shardKeyListMap = shardedConnectionHandler.getShardKeyMapping(context);

        final MethodCallCtx callCtx = CtxHelper.get(context, MethodCallCtx.class);
        List<Object> resList = new ArrayList<Object>(shardKeyListMap.size());
        for (Map.Entry<DataSource, Collection<Object>> entry : shardKeyListMap.entrySet()) {
            DataSource ds = entry.getKey();
            Collection<Object> keys = entry.getValue();
            //noinspection unchecked
            context.put(KEY_LIST_CONTEXT_VALUE, JoinListParameter.join(keys));
            Connection oldConnection = ConnectionHandlerHelper.setConnection(context, ds.getConnection());
            try {
                assert oldConnection == null;
                super.execute(context);
                resList.add(callCtx.getLastReturn());
            } finally {
                ConnectionHandlerHelper.closeConnection(context);
            }
        }

        @SuppressWarnings({"unchecked"})
        Object res = resultMerger.reduce(resList);
        callCtx.setLastReturn(res);
        return CONTINUE_PROCESSING;
    }

    @Override
    public void init(AnnotatedElement element, Annotation annotation) {
        MultiShardSelect multiShardSelect = element.getAnnotation(MultiShardSelect.class);
        resultMerger = injector.getInstance(multiShardSelect.resultMerger());
        super.init(element, multiShardSelect.value());
    }
}
