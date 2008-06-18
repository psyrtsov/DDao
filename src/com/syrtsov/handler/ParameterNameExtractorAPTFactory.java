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

package com.syrtsov.handler;

import com.sun.mirror.apt.AnnotationProcessor;
import com.sun.mirror.apt.AnnotationProcessorEnvironment;
import com.sun.mirror.apt.AnnotationProcessorFactory;
import com.sun.mirror.apt.AnnotationProcessors;
import com.sun.mirror.declaration.AnnotationTypeDeclaration;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

/**
 * Created by Pavel Syrtsov
 * Date: Sep 29, 2007
 * Time: 10:32:00 PM
 */
public class ParameterNameExtractorAPTFactory implements AnnotationProcessorFactory {
    private final Collection<String> supportedOptions = Collections.emptyList();
    private final Collection<String> supportedAnnotationTypes = Collections.singleton("com.syrtsov.ddao.Dao");

    public Collection<String> supportedOptions() {
        return supportedOptions;
    }

    public Collection<String> supportedAnnotationTypes() {
        return supportedAnnotationTypes;
    }

    public AnnotationProcessor getProcessorFor(Set<AnnotationTypeDeclaration> annotationTypeDeclarations, AnnotationProcessorEnvironment annotationProcessorEnvironment) {
        if (annotationTypeDeclarations.isEmpty())
            return AnnotationProcessors.NO_OP;
        return new ParameterNameExtractorAnnotationProcessor(annotationTypeDeclarations, annotationProcessorEnvironment);
    }
}
