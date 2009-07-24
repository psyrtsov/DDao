package com.sf.ddao.cache.impl;

import com.sf.ddao.alinker.ALinker;
import com.sf.ddao.alinker.Context;
import com.sf.ddao.alinker.Factory;
import com.sf.ddao.alinker.Initializer;
import com.sf.ddao.alinker.initializer.DefaultInitializerManager;
import com.sf.ddao.alinker.initializer.InitializerException;
import com.sf.ddao.alinker.initializer.InitializerService;
import com.sf.ddao.cache.Cache;

/**
 * Created by pavel
 * Date: Jul 23, 2009
 * Time: 10:36:40 PM
 */
public abstract class CacheFactoryBase implements Factory<Cache>, Initializer<CacheFactoryProxy>, InitializerService {
    @Override
    public void register(ALinker aLinker, DefaultInitializerManager defaultInitializerManager) {
        aLinker.init(this);
        defaultInitializerManager.register(CacheFactoryProxy.class, this);
    }

    @Override
    public void init(ALinker aLinker, Context<CacheFactoryProxy> ctx, CacheFactoryProxy cacheFactoryProxy) throws InitializerException {
        cacheFactoryProxy.addRealFactory(this);
    }
}
