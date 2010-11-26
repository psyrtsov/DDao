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

package com.sf.ddao.alinker.factory;

import com.sf.ddao.alinker.ALinker;
import com.sf.ddao.alinker.Context;
import com.sf.ddao.alinker.Factory;
import com.sf.ddao.alinker.FactoryException;
import com.sf.ddao.alinker.initializer.InitializerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

/**
 * Created-By: Pavel Syrtsov
 * Date: Apr 10, 2008
 * Time: 8:01:56 PM
 */
public class DefaultFactoryManager implements FactoryManager, Factory<DefaultFactoryManager> {
    private static final Logger log = LoggerFactory.getLogger(DefaultFactoryManager.class.getName());

    /**
     * we have to use Context as key here since we want to be able to define factory by attaching
     * annotation to injection point, such as @Conf("propertyName") attached to String parameter
     * shoud invoke configuratoin factory
     */
    private final Map<Context, Factory> factoryCache = new HashMap<Context, Factory>();
    private final Map<Class, Factory> configuredFactoryMap = new HashMap<Class, Factory>();
    private final Factory defaultFactory;
    private final ALinker aLinker;

    public DefaultFactoryManager(ALinker aLinker, Factory defaultFactory) {
        log.debug("Create default factory manager");
        this.defaultFactory = defaultFactory;
        this.aLinker = aLinker;
        register(DefaultFactoryManager.class, this);
    }

    public DefaultFactoryManager(ALinker aLinker) {
        this(aLinker, new DefaultFactory());
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
        final Class subjClass = ctx.getSubjClass();
        // 1st try to see if there preconfigured factory for this class
        Factory factory = configuredFactoryMap.get(subjClass);
        if (factory != null) {
            log.debug("Use precofigured factory {} for context {}", factory, ctx);
            return factory;
        }
        // 2nd look to target element(such as attribute this value is going to be assigned to) annotations
        Class<? extends Factory> factoryClass = getFactoryClass(ctx.getAnnotations());
        if (factoryClass != null) {
            factory = aLinker.create(factoryClass, factoryClass);
            log.debug("Created factory {} for context {} based on target element annotation", factory, ctx);
            return factory;
        }
        // 3rd look into annotations for class that we are going to instantiate
        if (subjClass == null) {
            throw new FactoryException("Failed to find factory for " + ctx);
        }
        factoryClass = getFactoryClass(subjClass.getAnnotations());
        if (factoryClass != null) {
            factory = aLinker.create(factoryClass, factoryClass);
            log.debug("Created factory {} for context {} based on class annotation", factory, ctx);
            return factory;
        }
        log.debug("Use default factory for context {}", ctx);
        return defaultFactory;
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

    public synchronized <T> void register(Class<T> clazz, Factory<T> factory) {
        Factory old = configuredFactoryMap.put(clazz, factory);
        if (old != null) {
            String msg = "Failed ro register factory " + factory + " for class " + clazz
                    + ", this class already associated with " + old;
            throw new FactoryException(msg);
        }
    }

    public DefaultFactoryManager create(ALinker aLinker, Context<DefaultFactoryManager> ctx) throws FactoryException {
        return this;
    }
}
