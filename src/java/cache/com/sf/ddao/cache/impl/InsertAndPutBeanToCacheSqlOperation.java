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
import com.sf.ddao.cache.InsertAndPutBeanToCache;
import com.sf.ddao.cache.Name;
import com.sf.ddao.factory.StatementFactory;
import com.sf.ddao.factory.StatementFactoryException;
import com.sf.ddao.factory.StatementFactoryManager;
import com.sf.ddao.factory.param.ThreadLocalStatementParameter;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;

/**
 * Created by pavel
 * Date: Jul 22, 2009
 * Time: 6:57:37 PM
 */
public class InsertAndPutBeanToCacheSqlOperation implements SqlOperation {
    public static final String ID_FIELD_NAME = "id";
    private StatementFactory statementFactory;
    @Inject
    public ALinker aLinker;
    private Cache<String, Object> cache;
    private InsertAndPutBeanToCache annotation;

    public Object invoke(Connection connection, Method method, Object[] args) {
        try {
            long longRes = cache.incr(annotation.idKey(), 1);
            // store generated id in thread local to be used by insert
            ThreadLocalStatementParameter.put(ID_FIELD_NAME, longRes);

            PreparedStatement preparedStatement = statementFactory.createStatement(connection, args);
            preparedStatement.executeUpdate();
            preparedStatement.close();
            cache.set(annotation.prefix() + longRes, args[0]);
            final Class<?> returnType = method.getReturnType();
            if (returnType == Long.class || returnType == Long.TYPE) {
                return longRes;
            }
            if (returnType == Integer.class || returnType == Integer.TYPE) {
                return (int) longRes;
            }
            return null;
        } catch (Exception t) {
            throw new DaoException("Failed to execute sql operation for " + method, t);
        } finally {
            ThreadLocalStatementParameter.remove(ID_FIELD_NAME);
        }
    }

    public void init(Method method) throws InitializerException {
        annotation = method.getAnnotation(InsertAndPutBeanToCache.class);
        String cacheName = annotation.cache();
        final Context<Cache> context = CtxBuilder.create(Cache.class).add(Name.class, cacheName).get();
        try {
            //noinspection unchecked
            cache = aLinker.create(context);
            statementFactory = StatementFactoryManager.createStatementFactory(method, annotation.sql());
        } catch (FactoryException e) {
            throw new InitializerException("", e);
        } catch (StatementFactoryException e) {
            throw new InitializerException("Failed to initialize sql operation for " + method, e);
        }
    }
}
