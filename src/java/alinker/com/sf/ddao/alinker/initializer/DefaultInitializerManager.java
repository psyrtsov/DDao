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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * psdo: add class comments
 * Created-By: Pavel Syrtsov
 * Date: Apr 10, 2008
 * Time: 8:05:12 PM
 */
public class DefaultInitializerManager implements InitializerManager {
    private final Map<Context, List<Initializer>> initializerCache = new HashMap<Context, List<Initializer>>();
    private final Initializer defaultInitializer;

    public DefaultInitializerManager() {
        defaultInitializer = new DependencyInjector();
    }

    public synchronized <T> Iterable<Initializer> getInitializers(ALinker aLinker, Context<T> ctx) throws InitializerException {
        List<Initializer> list;
        try {
            list = initializerCache.get(ctx);
            if (list == null) {
                list = createInitializerList(aLinker, ctx);
                initializerCache.put(ctx, list);
            }
        } catch (FactoryException e) {
            throw new InitializerException(ctx.toString(), e);
        }
        return list;
    }

    private List<Initializer> createInitializerList(ALinker aLinker, Context ctx) throws FactoryException, InitializerException {
        List<Initializer> list = new ArrayList<Initializer>();
        // add default initializer 1st so that all dependencies are there to prceeed with next init steps
        list.add(defaultInitializer);
        Annotation[] annotations;
        Class subjClass = ctx.getSubjClass();
        if (subjClass != null) {
            annotations = subjClass.getAnnotations();
            addInitializers(aLinker, annotations, list);
        }
        annotations = ctx.getAnnotations();
        addInitializers(aLinker, annotations, list);
        return list;
    }

    private void addInitializers(ALinker aLinker, Annotation[] annotations, List<Initializer> list) throws FactoryException, InitializerException {
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
}
