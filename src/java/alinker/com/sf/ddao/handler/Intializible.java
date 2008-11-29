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

package com.sf.ddao.handler;

import com.sf.ddao.alinker.initializer.InitializerException;

import java.lang.annotation.Annotation;
import java.util.List;

/**
 * when framework creates InvocationHandler that implements this interface
 * it will invoke initialixe with context where it had been created.
 * <p/>
 * Created by Pavel Syrtsov
 * Date: Nov 28, 2007
 * Time: 6:06:48 PM
 */
public interface Intializible {
    /**
     * initialize this object to be used in context defined by parameters
     *
     * @param iFace      - interface that this InovationHandler shall be handling
     * @param annotation - annotation that been used to create this object
     * @param iFaceList  - output parameter , InvocationHandler can add here
     *                   additional interfaces that it can support
     * @throws SetupException when fails to setup, leaves object in unusable state
     */
    void init(Class<?> iFace, Annotation annotation, List<Class<?>> iFaceList) throws InitializerException;
}
