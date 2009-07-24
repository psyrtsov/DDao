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
import com.sf.ddao.cache.RemoveFromCache;
import com.sf.ddao.factory.StatementFactory;
import com.sf.ddao.factory.StatementFactoryException;
import com.sf.ddao.factory.StatementFactoryManager;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;

/**
 * Created by pavel
 * Date: Jul 22, 2009
 * Time: 9:00:31 PM
 */
public class RemoveFromCacheSqlOperation implements SqlOperation {
    private StatementFactory[] statementFactoryList;
    @Inject
    public ALinker aLinker;
    private Cache<String, Object> cache;
    private RemoveFromCache annotation;

    public Object invoke(Connection connection, Method method, Object[] args) {
        try {
            String key = annotation.prefix() + args[0].toString();
            cache.delete(key);
            for (StatementFactory statementFactory : statementFactoryList) {
                PreparedStatement preparedStatement = statementFactory.createStatement(connection, args);
                preparedStatement.executeUpdate();
                preparedStatement.close();
            }
            return null;
        } catch (Exception t) {
            throw new DaoException("Failed to execute sql operation for " + method, t);
        }
    }

    public void init(Method method) throws InitializerException {
        annotation = method.getAnnotation(RemoveFromCache.class);
        String cacheName = annotation.cache();
        final Context<Cache> context = CtxBuilder.create(Cache.class).add(Name.class, cacheName).get();
        try {
            //noinspection unchecked
            cache = aLinker.create(context);
            final String[] sqlList = annotation.sql();
            statementFactoryList = new StatementFactory[sqlList.length];
            for (int i = 0; i < sqlList.length; i++) {
                statementFactoryList[i] = StatementFactoryManager.createStatementFactory(method, sqlList[i]);
            }
        } catch (FactoryException e) {
            throw new InitializerException("", e);
        } catch (StatementFactoryException e) {
            throw new InitializerException("Failed to initialize sql operation for " + method, e);
        }
    }
}
