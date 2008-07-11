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

package com.syrtsov.shards;

import com.syrtsov.alinker.initializer.InitializerException;
import com.syrtsov.alinker.inject.Inject;
import com.syrtsov.ddao.DaoException;
import com.syrtsov.ddao.conn.ConnectionHandlerHelper;
import com.syrtsov.ddao.conn.JNDIDataSourceHandler;
import com.syrtsov.handler.Intializible;
import org.apache.commons.beanutils.PropertyUtils;

import javax.naming.InitialContext;
import javax.sql.DataSource;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

/**
 * psdo: add class comments
 * Created-By: Pavel Syrtsov
 * Date: Apr 10, 2008
 * Time: 9:39:39 PM
 */
// psfix: how do we deal with multishard requests?
public class ShardedJNDIDataSourceHandler extends ConnectionHandlerHelper implements Intializible {
    private final Comparator<Comparable> shardComparator = new Comparator<Comparable>() {
        public int compare(Comparable comparable1, Comparable comparable2) {
            if (comparable1 instanceof Shard) {
                Shard shard = (Shard) comparable1;
                return shard.compareTo(comparable2);
            }
            Shard shard = (Shard) comparable2;
            return -shard.compareTo(comparable1);
        }
    };

    private SortedMap<Shard, Shard> shardMap = new TreeMap<Shard, Shard>(shardComparator);
    private ShardControlDao shardControlDao;

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Connection connection = getConnection(method, args);
        return invoke(connection, method, args);
    }

    public Connection createConnection(Method method, Object[] args) throws SQLException {
        Comparable shardKey = getShardKey(method, args);
        //noinspection SuspiciousMethodCalls
        Shard shard = shardMap.get(shardKey);
        DataSource ds = shard.getDataSource();
        return ds.getConnection();
    }

    private Comparable getShardKey(Method method, Object[] args) {
        Annotation[][] parametersAnnotations = method.getParameterAnnotations();
        for (int i = 0; i < parametersAnnotations.length; i++) {
            Annotation[] parameterAnnotations = parametersAnnotations[i];
            for (Annotation parameterAnnotation : parameterAnnotations) {
                if (parameterAnnotation instanceof ShardKey) {
                    ShardKey shardKey = (ShardKey) parameterAnnotation;
                    return extractShardKey(shardKey.value(), args[i]);
                }
            }
        }
        throw new ShardException("Expected parameter with annotation " + ShardKey.class + " at method " + method);
    }

    private Comparable extractShardKey(String name, Object shardKey) {
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
        return (Comparable) shardKey;
    }

    public void init(Class<?> iFace, Annotation annotation, List<Class<?>> iFaceList) throws InitializerException {
        ShardedJNDIDao daoAnnotation = (ShardedJNDIDao) annotation;
        String shardSetKey = daoAnnotation.value();
        List<Shard> shardList = shardControlDao.getShardList(shardSetKey);
        // make sure we deal with situation when we have few shards on same DataSource
        Map<String, DataSource> dsMap = new HashMap<String, DataSource>();
        for (Shard shard : shardList) {
            String dsName = shard.getDsName();
            DataSource ds = dsMap.get(dsName);
            if (ds == null) {
                ds = locateDataSource(dsName);
                dsMap.put(dsName, ds);
            }
            shard.setDataSource(ds);
            shardMap.put(shard, shard);
        }
        super.init(iFace, annotation, iFaceList);
    }

    private DataSource locateDataSource(String dsName) {
        try {
            InitialContext ic = new InitialContext(new Hashtable());
            return (DataSource) ic.lookup(JNDIDataSourceHandler.DS_CTX_PREFIX + dsName);
        } catch (Exception e) {
            throw new DaoException("Failed to find DataSource " + dsName, e);
        }
    }

    @Inject
    public void setShardControlDao(ShardControlDao shardControlDao) {
        this.shardControlDao = shardControlDao;
    }

}