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

package com.sf.ddao.alinker.inject;

import com.sf.ddao.alinker.ALinker;
import com.sf.ddao.alinker.Context;
import com.sf.ddao.alinker.Initializer;
import com.sf.ddao.alinker.initializer.InitializerException;
import com.sf.ddao.utils.Annotations;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * psdo: add class comments
 * Created-By: Pavel Syrtsov
 * Date: Apr 10, 2008
 * Time: 10:41:27 PM
 */
public class DependencyInjector<T> implements Initializer<T> {
    public void init(ALinker aLinker, Context<T> ctx, T subj) throws InitializerException {
        Annotation annotation = null;
        try {
            Class<?> aClass = subj.getClass();
            Field[] fields = aClass.getFields();
            for (Field field : fields) {
                annotation = Annotations.findAnnotation(field, Inject.class);
                if (annotation != null) {
                    injectField(aLinker, subj, field);
                }
            }
            Method[] methods = aClass.getMethods();
            for (Method method : methods) {
                annotation = Annotations.findAnnotation(method, Inject.class);
                if (annotation != null) {
                    injectMethod(aLinker, subj, method);
                }
            }
        } catch (Exception e) {
            throw new InitializerException("Failed to inject object " + subj + " annotated with " + annotation, e);
        }
    }

    @SuppressWarnings({"unchecked"})
    public static void injectField(ALinker aLinker, Object subj, Field field) throws InitializerException {
        try {
            Context ctx = new Context(field.getType(), field.getDeclaredAnnotations());
            Object obj = aLinker.create(ctx);
            field.set(subj, obj);
        } catch (Exception e) {
            throw new InitializerException("Failed to inject field " + field, e);
        }
    }

    @SuppressWarnings({"unchecked"})
    public static void injectMethod(ALinker aLinker, Object subj, Method method) throws InitializerException {
        try {
            final Annotation[][] parameterAnnotations = method.getParameterAnnotations();
            final Class<?>[] parameterTypes = method.getParameterTypes();
            Object args[] = new Object[parameterTypes.length];
            for (int i = 0; i < args.length; i++) {
                Context ctx = new Context(parameterTypes[i], parameterAnnotations[i]);
                Object arg = aLinker.create(ctx);
                args[i] = arg;
            }
            method.invoke(subj, args);
        } catch (Exception e) {
            throw new InitializerException("Failed to inject with method " + method, e);
        }
    }

    @SuppressWarnings({"unchecked"})
    public static Object injectConstructor(ALinker aLinker, Constructor constructor) throws InitializerException {
        try {
            final Annotation[][] parameterAnnotations = constructor.getParameterAnnotations();
            final Class<?>[] parameterTypes = constructor.getParameterTypes();
            Object args[] = new Object[parameterTypes.length];
            for (int i = 0; i < args.length; i++) {
                Context ctx = new Context(parameterTypes[i], parameterAnnotations[i]);
                Object arg = aLinker.create(ctx);
                args[i] = arg;
            }
            return constructor.newInstance(args);
        } catch (Exception e) {
            throw new InitializerException("Failed to create with injected constructor " + constructor, e);
        }
    }
}
