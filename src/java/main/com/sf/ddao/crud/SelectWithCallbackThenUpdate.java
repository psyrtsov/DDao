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

package com.sf.ddao.crud;

import com.sf.ddao.chain.CommandAnnotation;
import com.sf.ddao.crud.ops.SelectWithCallbackThenUpdateSqlOperation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation SelectWithCallbackThenUpdate allows to attach select statement
 * followed by update statement to ddao method.
 * <p/>
 * The bean returned by select is passed in to the method parameter implementing
 * <code>UpdateCallback</code>. After calling the callback, the bean is passed to
 * update statement.
 * <p/>
 * User: Tomi Joki-Korpela
 * Date: Feb 4, 2011
 * Time: 9:12:57 PM
 * <p/>
 * @See UpdateCallbackDao
 * @See UpdateCallback
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@CommandAnnotation(SelectWithCallbackThenUpdateSqlOperation.class)
//psdo: problem with this approach is that sequence has to be in same shard as data table
public @interface SelectWithCallbackThenUpdate {
    public abstract String[] value();
}
