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

package com.sf.ddao.factory.param;

import com.sf.ddao.alinker.ALinker;
import com.sf.ddao.alinker.factory.Singleton;
import com.sf.ddao.alinker.inject.Inject;

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
    private final ALinker aLinker;

    @Inject
    public ParameterFactory(ALinker aLinker) {
        this.aLinker = aLinker;
        init();
    }

    public void init() {
        final ServiceLoader<ParameterService> parameterServiceServiceLoader = ServiceLoader.load(ParameterService.class);
        for (ParameterService parameterService : parameterServiceServiceLoader) {
            aLinker.init(parameterService);
            parameterService.register(this);
        }
    }

    public Parameter createStatementParameter(AnnotatedElement element, String name) throws ParameterException {
        try {
            final int colonIdx = name.indexOf(':');
            if (colonIdx > 0) {
                String factoryName = name.substring(0, colonIdx);
                String paramName = name.substring(colonIdx + 1);
                final ParameterService parameterService = paramTypeMap.get(factoryName);
                return parameterService.create(element, factoryName, paramName);
            }
            Parameter param = aLinker.create(DefaultParameter.class);
            param.init(element, name);
            return param;
        } catch (Exception e) {
            throw new ParameterException("Failed to create parameter '" + name + "'", e);
        }
    }

    public void register(String funcName, ParameterService parameterService) {
        paramTypeMap.put(funcName, parameterService);
    }
}
