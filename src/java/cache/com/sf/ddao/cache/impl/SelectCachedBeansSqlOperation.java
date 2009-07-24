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

package com.sf.ddao.cache.impl;

import com.sf.ddao.DaoException;
import com.sf.ddao.SqlOperation;
import com.sf.ddao.alinker.ALinker;
import com.sf.ddao.alinker.Context;
import com.sf.ddao.alinker.CtxBuilder;
import com.sf.ddao.alinker.FactoryException;
import com.sf.ddao.alinker.initializer.InitializerException;
import com.sf.ddao.alinker.inject.Inject;
import com.sf.ddao.cache.Cache;
import com.sf.ddao.cache.Name;
import com.sf.ddao.cache.SelectCachedBeans;
import com.sf.ddao.factory.StatementFactory;
import com.sf.ddao.factory.StatementFactoryException;
import com.sf.ddao.factory.StatementFactoryManager;

import java.lang.reflect.Method;
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
public class SelectCachedBeansSqlOperation implements SqlOperation {
    private StatementFactory statementFactory;
    @Inject
    public ALinker aLinker;
    private Cache<String, Object> cache;

    public Object invoke(Connection connection, Method method, Object[] args) {
        try {
            PreparedStatement preparedStatement = statementFactory.createStatement(connection, args);
            ResultSet resultSet = preparedStatement.executeQuery();
            List<String> keyList = new ArrayList<String>();
            while (resultSet.next()) {
                final String key = resultSet.getString(1);
                keyList.add(key);
            }
            resultSet.close();
            preparedStatement.close();
            return cache.getMulti(keyList);
        } catch (Exception t) {
            throw new DaoException("Failed to execute sql operation for " + method, t);
        }
    }

    public void init(Method method) throws InitializerException {
        SelectCachedBeans selectCachedBeans = method.getAnnotation(SelectCachedBeans.class);
        String cacheName = selectCachedBeans.cache();
        final Context<Cache> context = CtxBuilder.create(Cache.class).add(Name.class, cacheName).get();
        try {
            //noinspection unchecked
            cache = aLinker.create(context);
        } catch (FactoryException e) {
            throw new InitializerException("", e);
        }
        init(method, selectCachedBeans.sql());
    }

    public void init(Method method, String sql) throws InitializerException {
        try {
            statementFactory = StatementFactoryManager.createStatementFactory(method, sql);
        } catch (StatementFactoryException e) {
            throw new InitializerException("Failed to initialize sql operation for " + method, e);
        }
    }
}
