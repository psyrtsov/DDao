package com.sf.ddao.cache.impl;

import com.sf.ddao.alinker.*;
import com.sf.ddao.cache.Cache;
import com.sf.ddao.cache.Name;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by pavel
 * Date: Jul 23, 2009
 * Time: 2:20:41 PM
 */
public class CacheFactoryProxy implements CachingFactory<Cache> {
    private final Map<String, Cache> map = new HashMap<String, Cache>();
    private final List<Factory<Cache>> factoryList = new ArrayList<Factory<Cache>>();

    public static String getName(Context<Cache> ctx) {
        Annotation[] annotations = ctx.getAnnotations();
        for (Annotation annotation : annotations) {
            if (annotation instanceof Name) {
                return ((Name) annotation).value();
            }
        }
        if (ctx.getDestination() instanceof Field) {
            Field field = (Field) ctx.getDestination();
            return field.getName();
        }
        return null;
    }

    @Override
    public Cache getCachedObject(Context<Cache> ctx) {
        String name = getName(ctx);
        return map.get(name);
    }

    @Override
    public Cache create(ALinker aLinker, Context<Cache> ctx) throws FactoryException {
        for (Factory<Cache> cacheFactory : factoryList) {
            Cache res = cacheFactory.create(aLinker, ctx);
            if (res != null) {
                String name = getName(ctx);
                map.put(name, res);
                return res;
            }
        }
        return null;
    }

    public void addRealFactory(Factory<Cache> realFactory) {
        factoryList.add(realFactory);
    }
}
