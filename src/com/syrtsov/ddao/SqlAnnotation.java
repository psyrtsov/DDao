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

package com.syrtsov.ddao;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * SqlAnotation have to be attached to annotation that defines new Dao operation.
 * Operation will be executed by instance of class supplied as value attribute of
 * this annotation.
 * <p/>
 * Created by: Pavel Syrtsov
 * Date: Apr 2, 2007
 * Time: 12:09:39 AM
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.ANNOTATION_TYPE})
public @interface SqlAnnotation {
    /**
     * @return class object that implements SQLOperation interface.
     *         This is the class that will be instantiated to execute operation
     *         associated with target annotation.
     */
    Class<? extends SqlOperation> value();
}
