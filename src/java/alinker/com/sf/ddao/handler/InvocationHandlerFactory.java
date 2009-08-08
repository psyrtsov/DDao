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

package com.sf.ddao.handler;

import com.sf.ddao.alinker.ALinker;
import com.sf.ddao.alinker.CachingFactory;
import com.sf.ddao.alinker.Context;
import com.sf.ddao.alinker.FactoryException;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

/**
 * Note : all factories are singletons that stored in factory manager upon creation
 * HandlerFactory is utility class that creates chain of InvocationHandler
 * according to annotations attached to given interface.
 * <p/>
 * User: Pavel Syrtsov
 * Date: Apr 1, 2007
 * Time: 10:38:58 PM
 */
public class InvocationHandlerFactory<T> implements CachingFactory<T> {
    /**
     * since this factory going to be singleton according to factory manager's code
     * caching of handler instance in this attribute makes it also singleton
     */
    private T cachedProxy = null;

    public T create(ALinker aLinker, Context<T> ctx) throws FactoryException {
        Class<T> iFace = ctx.getSubjClass();
        List<Class<?>> iFaceList = new ArrayList<Class<?>>();
        iFaceList.add(iFace);
        for (Annotation annotation : iFace.getAnnotations()) {
            InvocationHandlerAnnotation ha = annotation.annotationType().getAnnotation(InvocationHandlerAnnotation.class);
            if (ha != null) {
                Class<? extends InvocationHandler> clazz = ha.value();
                try {
                    InvocationHandler ih = aLinker.create(clazz, null);
                    if (ih instanceof Intializible) {
                        Intializible conf = (Intializible) ih;
                        conf.init(iFace, annotation);
                    }
                    Class[] iFaces = iFaceList.toArray(new Class[iFaceList.size()]);
                    //noinspection unchecked
                    T res = (T) Proxy.newProxyInstance(iFace.getClassLoader(), iFaces, ih);
                    if (ha.singleton()) {
                        cachedProxy = res;
                    }
                    return res;
                } catch (Exception e) {
                    throw new FactoryException("Failed to create " + clazz, e);
                }
            }
        }
        throw new FactoryException("Failed to find annotation that has " + InvocationHandlerAnnotation.class.getSimpleName() + " attached for " + iFace);
    }

    public T getCachedObject(Context<T> ctx) {
        return cachedProxy;
    }
}
