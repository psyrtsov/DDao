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

package com.sf.ddao.utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;

/**
 * Created-By: Pavel Syrtsov
 * Date: Apr 10, 2008
 * Time: 3:49:45 PM
 */
public class Annotations {

    public static int findParameterAnnotatedWith(Method method, final Class<? extends Annotation> annotationClass) {
        final Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        for (int i = 0; i < parameterAnnotations.length; i++) {
            Annotation[] parameterAnnotation = parameterAnnotations[i];
            for (Annotation a : parameterAnnotation) {
                if (a.annotationType() == annotationClass) {
                    return i;
                }
            }
        }
        return -1;
    }

    public static <A extends Annotation> A findAnnotation(AnnotatedElement annotatedElement, Class<A> expectedAnnotation) {
        A foundAnnotation = annotatedElement.getAnnotation(expectedAnnotation);
        if (foundAnnotation != null) {
            return foundAnnotation;
        }
        Annotation[] annotations = annotatedElement.getAnnotations();
        return findAnnotationAnnotatedWith(annotations, expectedAnnotation);
    }

    /**
     * @param annotations
     * @param expectedAnnotation
     * @return
     */
    public static <A extends Annotation> A findAnnotationAnnotatedWith(Annotation[] annotations, Class<A> expectedAnnotation) {
        if (annotations == null) {
            return null;
        }
        for (Annotation annotation : annotations) {
            A foundAnnotation = annotation.annotationType().getAnnotation(expectedAnnotation);
            if (foundAnnotation != null) {
                return foundAnnotation;
            }
        }
        return null;
    }
}
