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

package com.syrtsov.ddao.alinker;

import java.lang.annotation.Annotation;
import java.util.Arrays;

/**
 * psdo: add class comments
 * Created-By: Pavel Syrtsov
 * Date: Apr 10, 2008
 * Time: 8:07:22 PM
 */
public class Context<T> {
    private final Class<T> subjClass;
    private final Annotation[] annotations;

    public Context(Class<T> subjClass, Annotation[] annotations) {
        this.subjClass = subjClass;
        this.annotations = annotations;
    }

    public Class<T> getSubjClass() {
        return subjClass;
    }

    public Annotation[] getAnnotations() {
        return annotations;
    }

    @SuppressWarnings({"RedundantIfStatement"})
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Context context = (Context) o;

        if (subjClass != null ? !subjClass.equals(context.subjClass) : context.subjClass != null) return false;
        if (!Arrays.equals(annotations, context.annotations)) return false;

        return true;
    }

    public int hashCode() {
        int result;
        result = (subjClass != null ? subjClass.hashCode() : 0);
        result = 31 * result + (annotations != null ? Arrays.hashCode(annotations) : 0);
        return result;
    }
}
