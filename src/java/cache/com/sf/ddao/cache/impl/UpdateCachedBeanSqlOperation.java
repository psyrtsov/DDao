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
import com.sf.ddao.cache.UpdateCachedBean;

import java.lang.reflect.Method;
import java.sql.Connection;

/**
 * Created by pavel
 * Date: Jul 22, 2009
 * Time: 8:46:34 PM
 */
public class UpdateCachedBeanSqlOperation implements SqlOperation {
    public static final String ID_FIELD_NAME = "id";
    @Inject
    public ALinker aLinker;
    private Cache<String, Object> cache;
    private UpdateCachedBean annotation;

    public Object invoke(Connection connection, Method method, Object[] args) {
        try {
            String key = annotation.prefix() + args[0].toString();
            cache.set(key, args[1]);
            return null;
        } catch (Exception t) {
            throw new DaoException("Failed to execute sql operation for " + method, t);
        }
    }

    public void init(Method method) throws InitializerException {
        annotation = method.getAnnotation(UpdateCachedBean.class);
        String cacheName = annotation.cache();
        final Context<Cache> context = CtxBuilder.create(Cache.class).add(Name.class, cacheName).get();
        try {
            //noinspection unchecked
            cache = aLinker.create(context);
        } catch (FactoryException e) {
            throw new InitializerException("", e);
        }
    }
}
