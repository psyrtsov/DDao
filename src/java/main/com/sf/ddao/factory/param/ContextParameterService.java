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

import com.sf.ddao.alinker.ALinker;
import com.sf.ddao.alinker.inject.Link;

import java.lang.reflect.AnnotatedElement;

/**
 * Date: Oct 27, 2009
 * Time: 3:34:24 PM
 */
public class ContextParameterService implements ParameterService {
    public static final String FUNC_NAME = "ctx";
    @Link
    public ALinker aLinker;

    public void register(ParameterFactory parameterFactory) {
        parameterFactory.register(FUNC_NAME, this);
    }

    public Parameter create(AnnotatedElement element, String funcName, String paramName) {
        final ContextParameter res = aLinker.create(ContextParameter.class);
        res.init(element, paramName);
        return res;
    }
}
