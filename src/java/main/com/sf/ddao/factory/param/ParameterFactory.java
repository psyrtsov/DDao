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
import javax.inject.Singleton;
import java.lang.reflect.AnnotatedElement;
import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

/**
 * Created-By: Pavel Syrtsov
 * Date: Apr 10, 2008
 * Time: 3:29:23 PM
 */
@Singleton
public class ParameterFactory {
    private final Map<String, ParameterService> paramTypeMap = new HashMap<String, ParameterService>();
    private final Injector aLinker;

    @Inject
    public ParameterFactory(Injector aLinker) {
        this.aLinker = aLinker;
        final ServiceLoader<ParameterService> parameterServiceServiceLoader = ServiceLoader.load(ParameterService.class);
        for (ParameterService parameterService : parameterServiceServiceLoader) {
            this.aLinker.injectMembers(parameterService);
            parameterService.register(this);
        }
    }

    public ParameterHandler createStatementParameter(AnnotatedElement element, String name, boolean isRef) throws ParameterException {
        try {
            final int colonIdx = name.indexOf(':');
            if (colonIdx > 0) {
                String factoryName = name.substring(0, colonIdx);
                String paramName = name.substring(colonIdx + 1);
                final ParameterService parameterService = paramTypeMap.get(factoryName);
                if (parameterService == null) {
                    throw new Exception("Factory is not defined for '" + factoryName + "':" + paramTypeMap);
                }
                return parameterService.create(element, factoryName, paramName, isRef);
            }
            ParameterHandler param = aLinker.getInstance(DefaultParameter.class);
            param.init(element, name, isRef);
            return param;
        } catch (Exception e) {
            throw new ParameterException("Failed to create parameter '" + name + "'", e);
        }
    }

    public void register(String funcName, ParameterService parameterService) {
        paramTypeMap.put(funcName, parameterService);
    }
}
