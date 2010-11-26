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

package com.sf.ddao.chain;

import com.sf.ddao.alinker.ALinker;
import com.sf.ddao.alinker.CachingFactory;
import com.sf.ddao.alinker.Context;
import com.sf.ddao.alinker.FactoryException;
import com.sf.ddao.alinker.factory.Singleton;

import java.lang.reflect.Proxy;

/**
 * factory is registered one instance per class/annotaion combination
 * <p/>
 * Date: Oct 23, 2009
 * Time: 2:37:52 PM
 */
public class ChainHandlerFactory<T> implements CachingFactory<T> {
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
