package com.sf.ddao.chain;

import com.sf.ddao.alinker.ALinker;
import com.sf.ddao.alinker.CachingFactory;
import com.sf.ddao.alinker.Context;
import com.sf.ddao.alinker.FactoryException;
import com.sf.ddao.alinker.factory.Singleton;

import java.lang.reflect.Proxy;

/**
 * Date: Oct 23, 2009
 * Time: 2:37:52 PM
 */
public class ChainHandlerFactory<T> implements CachingFactory<T> {
    /**
     * since this factory going to be singleton according to factory manager's code
     * caching of handler instance in this attribute makes it also singleton
     */
    private T cachedProxy = null;

    public T create(ALinker aLinker, Context<T> ctx) throws FactoryException {
        Class<T> iFace = ctx.getSubjClass();
        final Class[] iFaceList = {iFace};
        try {
            ChainInvocationHandler cih = aLinker.create(ChainInvocationHandler.class, null);
            cih.init(iFace);
            //noinspection unchecked
            T res = (T) Proxy.newProxyInstance(iFace.getClassLoader(), iFaceList, cih);
            Singleton singleton = iFace.getAnnotation(Singleton.class);
            if (singleton != null) {
                cachedProxy = res;
            }
            return res;
        } catch (Exception e) {
            throw new FactoryException("Failed to create " + iFace, e);
        }
    }

    public T getCachedObject(Context<T> ctx) {
        return cachedProxy;
    }
}
