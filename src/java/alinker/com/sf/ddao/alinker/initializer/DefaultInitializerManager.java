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

package com.sf.ddao.alinker.initializer;

import com.sf.ddao.alinker.ALinker;
import com.sf.ddao.alinker.Context;
import com.sf.ddao.alinker.FactoryException;
import com.sf.ddao.alinker.Initializer;
import com.sf.ddao.alinker.inject.DependencyInjector;

import java.lang.annotation.Annotation;
import java.util.*;

/**
 * psdo: add class comments
 * Created-By: Pavel Syrtsov
 * Date: Apr 10, 2008
 * Time: 8:05:12 PM
 */
public class DefaultInitializerManager implements InitializerManager {
    private final Map<Class, List<Initializer>> initializerCache = new HashMap<Class, List<Initializer>>();
    private final Initializer defaultInitializer;
    private final ALinker aLinker;

    public DefaultInitializerManager(ALinker aLinker) {
        this.aLinker = aLinker;
        defaultInitializer = new DependencyInjector();
    }

    public void init() {
        final ServiceLoader<InitializerService> initializerServiceServiceLoader = ServiceLoader.load(InitializerService.class);
        for (InitializerService initializerService : initializerServiceServiceLoader) {
            initializerService.register(aLinker, this);
        }
    }

    public synchronized <T> Iterable<Initializer> getInitializers(Context<T> ctx) {
        return getList(ctx.getSubjClass());
    }

    private List<Initializer> getList(Class<?> subjClass) {
        List<Initializer> list = initializerCache.get(subjClass);
        if (list == null) {
            list = createInitializerList(subjClass);
            initializerCache.put(subjClass, list);
        }
        return list;
    }

    private List<Initializer> createInitializerList(Class subjClass) throws FactoryException, InitializerException {
        List<Initializer> list = new ArrayList<Initializer>();
        // add default initializer 1st so that all dependencies are there to prceeed with next init steps
        list.add(defaultInitializer);
        Annotation[] annotations;
        annotations = subjClass.getAnnotations();
        addInitializers(annotations, list);
        return list;
    }

    private void addInitializers(Annotation[] annotations, List<Initializer> list) throws FactoryException, InitializerException {
        if (annotations == null) {
            return;
        }
        for (Annotation annotation : annotations) {
            if (annotation instanceof UseInitializer) {
                UseInitializer useInitializer = (UseInitializer) annotation;
                Class<? extends Initializer> aClass = useInitializer.value();
                Initializer initializer = aLinker.create(aClass, aClass);
                list.add(initializer);
            }
        }
        for (Annotation annotation : annotations) {
            UseInitializer useInitializer = annotation.annotationType().getAnnotation(UseInitializer.class);
            if (useInitializer != null) {
                Class<? extends Initializer> aClass = useInitializer.value();
                Initializer initializer = aLinker.create(aClass, aClass);
                list.add(initializer);
            }
        }
    }

    public void register(Class clazz, Initializer initializer) {
        getList(clazz).add(initializer);
    }
}
