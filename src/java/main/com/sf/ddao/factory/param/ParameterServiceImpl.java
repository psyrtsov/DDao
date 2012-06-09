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

package com.sf.ddao.factory.param;

import com.google.inject.Injector;

import javax.inject.Inject;
import java.lang.reflect.AnnotatedElement;
import java.util.HashMap;
import java.util.Map;

/**
 * Created-By: Pavel Syrtsov
 * Date: Apr 10, 2008
 * Time: 4:14:54 PM
 */
public class ParameterServiceImpl implements ParameterService {
    public static final Map<String, Class<? extends ParameterHandler>> classMap = new HashMap<String, Class<? extends ParameterHandler>>() {{
        put(ContextParameter.CTX, ContextParameter.class);
        put(JoinListParameter.JOIN_LIST, JoinListParameter.class);
        put(ThreadLocalParameter.THREAD_LOCAL, ThreadLocalParameter.class);
        put(ForwardParameter.FWD, ForwardParameter.class);
    }};
    @Inject
    public Injector injector;

    public void register(ParameterFactory parameterFactory) {
        for (String name : classMap.keySet()) {
            parameterFactory.register(name, this);
        }
    }

    public ParameterHandler create(AnnotatedElement element, String funcName, String paramName, boolean isRef) {
        final Class<? extends ParameterHandler> aClass = classMap.get(funcName);
        final ParameterHandler res = injector.getInstance(aClass);
        res.init(element, paramName, isRef);
        return res;
    }
}