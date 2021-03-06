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

package com.sf.ddao;

import com.sf.ddao.chain.CommandAnnotation;
import com.sf.ddao.ops.DeleteSqlOperation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation Delete allows to attach delete statement to ddao method.
 * <p/>
 * Created by: Pavel Syrtsov
 * Date: Apr 1, 2007
 * Time: 11:58:13 PM
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@CommandAnnotation(DeleteSqlOperation.class)
public @interface Delete {
    /**
     * @return text of the template for delete statement
     */
    String value();
}
