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

import org.apache.commons.chain.Chain;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.chain.impl.ChainBase;
import org.apache.commons.chain.impl.ContextBase;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by pavel
 * Date: Aug 6, 2009
 * Time: 2:29:05 PM
 */
class MethodInvocationHandler {
    public static final Set<Class> notNullableTypes = new HashSet<Class>() {{
        add(Byte.TYPE);
        add(Short.TYPE);
        add(Integer.TYPE);
        add(Long.TYPE);
        add(Float.TYPE);
        add(Double.TYPE);
        add(Boolean.TYPE);
        add(Character.TYPE);
    }};
    private final Method method;
    private final Chain chain;
    private int contextParamIndex = -1;
    private final boolean isNullReturnDisallowed;

    public MethodInvocationHandler(Method method, List<Command> commands) {
        isNullReturnDisallowed = notNullableTypes.contains(method.getReturnType());
        this.method = method;
        this.chain = new ChainBase(commands);
        final Annotation[][] parametersAnnotations = method.getParameterAnnotations();
        for (int i = 0; i < parametersAnnotations.length; i++) {
            for (Annotation parameterAnnotation : parametersAnnotations[i]) {
                if (parameterAnnotation.annotationType().equals(UseContext.class)) {
                    contextParamIndex = i;
                    return;
                }
            }
        }
    }

    public Object invoke(Object[] args) throws Throwable {
        Context context = createContext(args);
        final MethodCallCtx callCtx = new MethodCallCtx(args, method);
        CtxHelper.put(context, MethodCallCtx.class, callCtx);
        chain.execute(context);
        if (Context.class.isAssignableFrom(method.getReturnType())) {
            return context;
        }
        final Object aReturn = callCtx.getLastReturn();
        if (aReturn == null && isNullReturnDisallowed) {
            throw new NullPointerException("Null value is not allowed for return type " + method.getReturnType());
        }
        return aReturn;
    }

    private Context createContext(Object[] args) {
        Context context;
        if (contextParamIndex >= 0) {
            context = (Context) args[contextParamIndex];
            if (context == null) {
                throw new NullPointerException("Context parameter is null!");
            }
        } else {
            context = new ContextBase();
        }
        return context;
    }
}
