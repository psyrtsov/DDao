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

package com.syrtsov.ddao.alinker.factory;

import com.syrtsov.ddao.alinker.ALinker;
import com.syrtsov.ddao.alinker.Context;
import com.syrtsov.ddao.alinker.Factory;
import com.syrtsov.ddao.alinker.FactoryException;

import java.lang.annotation.Annotation;

/**
 * Created-By: Pavel Syrtsov
 * Date: Apr 10, 2008
 * Time: 4:28:55 PM
 */
public class InjectValueFactory implements Factory {
    public Object create(ALinker nInjector, Context ctx) throws FactoryException {
        final Annotation[] annotations = ctx.getAnnotations();
        for (Annotation annotation : annotations) {
            if (annotation instanceof InjectValue) {
                InjectValue injectValue = (InjectValue) annotation;
                return injectValue.value();
            }
        }
        return null;
    }
}
