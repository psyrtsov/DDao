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

/**
 * psdo: add class comments
 * Created-By: Pavel Syrtsov
 * Date: Apr 10, 2008
 * Time: 3:49:45 PM
 */
public class Annotations {
    public static <A extends Annotation> A findAnnotation(AnnotatedElement annotatedElement, Class<A> expectedAnnotation) {
        A foundAnnotation = annotatedElement.getAnnotation(expectedAnnotation);
        if (foundAnnotation != null) {
            return foundAnnotation;
        }
        Annotation[] annotations = annotatedElement.getAnnotations();
        return findAnnotation(annotations, expectedAnnotation);
    }

    /**
     * todo: come up with better name
     *
     * @param annotations
     * @param expectedAnnotation
     * @return
     */
    public static <A extends Annotation> A findAnnotation(Annotation[] annotations, Class<A> expectedAnnotation) {
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
