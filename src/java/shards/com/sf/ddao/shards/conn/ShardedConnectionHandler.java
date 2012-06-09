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

package com.sf.ddao.shards.conn;

import com.google.inject.Injector;
import com.sf.ddao.chain.CtxHelper;
import com.sf.ddao.chain.Intializible;
import com.sf.ddao.chain.MethodCallCtx;
import com.sf.ddao.conn.ConnectionHandlerHelper;
import com.sf.ddao.shards.ShardException;
import com.sf.ddao.shards.ShardKey;
import com.sf.ddao.shards.ShardedDao;
import com.sf.ddao.shards.ShardingService;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.chain.Context;

import javax.inject.Inject;
import javax.sql.DataSource;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created-By: Pavel Syrtsov
 * Date: Apr 10, 2008
 * Time: 9:39:39 PM
 */
public class ShardedConnectionHandler extends ConnectionHandlerHelper implements Intializible {
    @Inject
    protected Injector injector;
    private final Map<Method, ShardKeyGetter> shardKeyGetterMap = new HashMap<Method, ShardKeyGetter>();
    protected ShardingService shardingService;

    @Override
    public boolean execute(Context context) throws Exception {
        CtxHelper.put(context, ShardedConnectionHandler.class, this);
        return super.execute(context);
    }

    public void init(AnnotatedElement element, Annotation annotation) {
        ShardedDao daoAnnotation = (ShardedDao) annotation;
        Class<? extends ShardingService> shardControlDaoClass = daoAnnotation.value();
        init((Class) element, shardControlDaoClass);
    }

    protected void init(Class daoClass, Class<? extends ShardingService> shardingServiceClass) {
        initShardKeys(daoClass);
        shardingService = injector.getInstance(shardingServiceClass);
    }

    protected void initShardKeys(Class clazz) {
        for (Method method : clazz.getMethods()) {
            ShardKeyGetter shardKeyGetter = createShardKeyGetter(method);
            if (shardKeyGetter != null) {
                shardKeyGetterMap.put(method, shardKeyGetter);
            }
        }
    }

    protected ShardKeyGetter createShardKeyGetter(Method method) {
        Annotation[][] parametersAnnotations = method.getParameterAnnotations();
        for (int i = 0; i < parametersAnnotations.length; i++) {
            Annotation[] parameterAnnotations = parametersAnnotations[i];
            for (Annotation parameterAnnotation : parameterAnnotations) {
                if (parameterAnnotation instanceof ShardKey) {
                    final ShardKey shardKey = (ShardKey) parameterAnnotation;
                    final int argIdx = i;
                    return new ShardKeyGetter() {
                        public Object getShardKey(Object[] args) {
                            return extractShardKey(shardKey.value(), args[argIdx]);
                        }
                    };
                }
            }
        }
        return null;
    }

    public Object extractShardKey(String name, Object shardKey) {
        if (name.length() > 0) {
            // if name defined then key is either mapped value or bean property
            if (shardKey instanceof Map) {
                shardKey = ((Map) shardKey).get(name);
            } else {
                try {
                    shardKey = PropertyUtils.getProperty(shardKey, name);
                } catch (Exception e) {
                    throw new ShardException("Failed to get shard key " + name + " from " + shardKey, e);
                }
            }
            if (shardKey == null) {
                throw new ShardException("Failed to find shard key ");
            }
        }
        return shardKey;
    }

    public static interface ShardKeyGetter {
        Object getShardKey(Object[] args);
    }

    @Override
    public Connection createConnection(Context context) throws SQLException {
        final MethodCallCtx callCtx = CtxHelper.get(context, MethodCallCtx.class);
        final ShardKeyGetter shardKeyGetter = shardKeyGetterMap.get(callCtx.getMethod());
        if (shardKeyGetter == null) {
            throw new ShardException("Expected parameter with annotation " + ShardKey.class + " at method " + callCtx.getMethod());
        }
        Object shardKey = shardKeyGetter.getShardKey(callCtx.getArgs());
        try {
            @SuppressWarnings({"unchecked"})
            DataSource ds = shardingService.getShard(shardKey, context);
            //noinspection SuspiciousMethodCalls
            return ds.getConnection();
        } catch (Exception e) {
            throw new SQLException("Failed to retrieve shard for key " + shardKey + (shardKey == null ? "" : "(" + shardKey.getClass() + ")"), e);
        }
    }

    public Map<DataSource, Collection<Object>> getShardKeyMapping(Context context) {
        final MethodCallCtx callCtx = CtxHelper.get(context, MethodCallCtx.class);
        final ShardKeyGetter shardKeyGetter = shardKeyGetterMap.get(callCtx.getMethod());
        if (shardKeyGetter == null) {
            throw new ShardException("Expected parameter with annotation " + ShardKey.class + " at method " + callCtx.getMethod());
        }
        Object shardKey = shardKeyGetter.getShardKey(callCtx.getArgs());
        if (!(shardKey instanceof Collection)) {
            throw new ShardException("Shard key at method " + callCtx.getMethod() + " has to be collection to be used with multi-shard query");
        }
        Collection shardKeyCollection = (Collection) shardKey;
        //noinspection unchecked
        return shardingService.getMultiShard(shardKeyCollection, context);
    }
}
