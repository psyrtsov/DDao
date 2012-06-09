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

import com.google.inject.Injector;
import org.apache.commons.chain.Command;

import javax.inject.Inject;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This invocation handler allows to enterpret few annotations attached to method as chain of comamnds to be executed
 * when method is invoked
 * <p/>
 */
public class ChainInvocationHandler implements InvocationHandler {
    Map<Method, MethodInvocationHandler> map = new HashMap<Method, MethodInvocationHandler>();

    @Inject
    public Injector injector;

    public Object invoke(Object o, Method method, Object[] args) throws Throwable {
        final MethodInvocationHandler methodHandler = map.get(method);
        return methodHandler.invoke(args);
    }

    public void init(Class<?> iFace) {
        List<Command> classLevelList = new ArrayList<Command>();
        addChainMemebers(iFace, classLevelList);
        final Method[] methods = iFace.getMethods();
        for (Method method : methods) {
            List<Command> list = new ArrayList<Command>(classLevelList.size() + method.getAnnotations().length);
            list.addAll(classLevelList);
            addChainMemebers(method, list);
            map.put(method, new MethodInvocationHandler(iFace, method, list));
        }
    }

    private void addChainMemebers(AnnotatedElement annotatedElement, List<Command> list) {
        for (Annotation annotation : annotatedElement.getAnnotations()) {
            final CommandAnnotation memberAnnotation = annotation.annotationType().getAnnotation(CommandAnnotation.class);
            if (memberAnnotation == null || memberAnnotation.value() == null) {
                continue;
            }
            final Command chainCommand = injector.getInstance(memberAnnotation.value());
            if (chainCommand instanceof Intializible) {
                Intializible intializible = (Intializible) chainCommand;
                intializible.init(annotatedElement, annotation);
            }
            list.add(chainCommand);
        }
    }

}
