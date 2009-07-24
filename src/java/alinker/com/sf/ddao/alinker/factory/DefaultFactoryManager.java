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

package com.sf.ddao.alinker.factory;

import com.sf.ddao.alinker.ALinker;
import com.sf.ddao.alinker.Context;
import com.sf.ddao.alinker.Factory;
import com.sf.ddao.alinker.FactoryException;
import com.sf.ddao.alinker.initializer.InitializerException;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

/**
 * psdo: add class comments
 * Created-By: Pavel Syrtsov
 * Date: Apr 10, 2008
 * Time: 8:01:56 PM
 */
public class DefaultFactoryManager implements FactoryManager {
    //psdo: switch to use Class as key here, make sure initializer takes in account all other context attributes when execueted
    private final Map<Context, Factory> factoryCache = new HashMap<Context, Factory>();
    private final Factory defaultFactory;

    public DefaultFactoryManager() {
        this.defaultFactory = new DefaultFactory();
    }

    public DefaultFactoryManager(Factory defaultFactory) {
        this.defaultFactory = defaultFactory;
    }

    public synchronized <T> Factory<T> getFactory(ALinker aLinker, Context<T> ctx) throws FactoryException {
        Factory factory;
        try {
            factory = factoryCache.get(ctx);
            if (factory == null) {
                factory = createFactory(aLinker, ctx);
                factoryCache.put(ctx, factory);
            }
        } catch (Exception e) {
            throw new FactoryException(ctx.toString(), e);
        }
        //noinspection unchecked
        return factory;
    }

    private Factory createFactory(ALinker aLinker, Context ctx) throws FactoryException, InitializerException {
        Factory factory;
        Class<? extends Factory> factoryClass = getFactoryClass(ctx);
        if (factoryClass != null) {
            factory = aLinker.create(factoryClass, factoryClass);
        } else {
            factory = defaultFactory;
        }
        return factory;
    }

    private Class<? extends Factory> getFactoryClass(Context ctx) throws FactoryException {
        Class<? extends Factory> factoryClass;
        Annotation[] annotations = ctx.getAnnotations();
        factoryClass = getFactoryClass(annotations);
        if (factoryClass != null) {
            return factoryClass;
        }
        Class subjClass = ctx.getSubjClass();
        if (subjClass == null) {
            throw new FactoryException("Failed to find factory for " + ctx);
        }

        annotations = subjClass.getAnnotations();
        factoryClass = getFactoryClass(annotations);
        return factoryClass;
    }

    private Class<? extends Factory> getFactoryClass(Annotation[] annotations) {
        if (annotations == null) {
            return null;
        }
        for (Annotation annotation : annotations) {
            if (annotation instanceof UseFactory) {
                UseFactory useFactory = (UseFactory) annotation;
                return useFactory.value();
            }
        }
        for (Annotation annotation : annotations) {
            UseFactory useFactory = annotation.annotationType().getAnnotation(UseFactory.class);
            if (useFactory != null) {
                return useFactory.value();
            }
        }
        return null;
    }
}
