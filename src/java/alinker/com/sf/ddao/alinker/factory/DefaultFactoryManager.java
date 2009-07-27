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
import java.util.ServiceLoader;

/**
 * psdo: add class comments
 * Created-By: Pavel Syrtsov
 * Date: Apr 10, 2008
 * Time: 8:01:56 PM
 */
public class DefaultFactoryManager implements FactoryManager {
    /**
     * we have to use Context as key here since we want to be able to define factory by attaching
     * annotation to injection point, such as @Conf("propertyName") attached to String parameter
     * shoud invoke configuratoin factory
     */
// psdo: we need to cache factories create based on subjClass only, to make sure they there is only one instance per class
    private final Map<Context, Factory> factoryCache = new HashMap<Context, Factory>();
    private final Map<Class, Factory> classFactoryMap = new HashMap<Class, Factory>();
    private final Factory defaultFactory;
    private final ALinker aLinker;

    public DefaultFactoryManager(Factory defaultFactory, ALinker aLinker) {
        this.defaultFactory = defaultFactory;
        this.aLinker = aLinker;
    }

    public DefaultFactoryManager(ALinker aLinker) {
        this.aLinker = aLinker;
        this.defaultFactory = new DefaultFactory();
    }

    public void init() {
        final ServiceLoader<FactoryService> factoryServiceServiceLoader = ServiceLoader.load(FactoryService.class);
        for (FactoryService factoryService : factoryServiceServiceLoader) {
            factoryService.register(aLinker, this);
        }
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
            factory = classFactoryMap.get(ctx.getSubjClass());
            if (factory == null) {
                factory = defaultFactory;
            }
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

    public <T> void register(Class<T> clazz, Factory<T> factory) {
        Factory old = classFactoryMap.put(clazz, factory);
        if (old != null) {
            //psdo: find better way to say it
            String msg = "Failed ro register factory " + factory + " for class " + clazz
                    + ", this class already associated with " + old;
            throw new FactoryException(msg);
        }
    }
}
