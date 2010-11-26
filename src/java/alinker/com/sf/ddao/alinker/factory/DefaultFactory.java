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
import com.sf.ddao.alinker.inject.DependencyInjector;
import com.sf.ddao.alinker.inject.Link;
import com.sf.ddao.utils.Annotations;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;

/**
 * Created-By: Pavel Syrtsov
 * Date: Apr 10, 2008
 * Time: 9:39:39 PM
 */
public class DefaultFactory implements Factory {
    public Object create(ALinker aLinker, Context ctx) throws FactoryException {
        Class<?> clazz = ctx.getSubjClass();
        if (ALinker.class.equals(clazz)) {
            return aLinker;
        }
        InstanceOf implementedBy = clazz.getAnnotation(InstanceOf.class);
        if (implementedBy != null) {
            clazz = implementedBy.value();
        }
        try {
            final Constructor[] constructors = clazz.getConstructors();
            for (Constructor constructor : constructors) {
                final Annotation annotation = Annotations.findAnnotation(constructor, Link.class);
                if (annotation != null) {
                    return DependencyInjector.injectConstructor(aLinker, constructor);
                }
            }
            // no constructors with injection, then fallback to parameterless constructor
            return clazz.newInstance();
        } catch (Exception e) {
            throw new FactoryException("Failed to create instance of " + clazz, e);
        }
    }
}
