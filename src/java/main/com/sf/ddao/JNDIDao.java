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
import com.sf.ddao.conn.JNDIDataSourceHandler;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation defines tnat interface logic that it is attached to
 * is Dao interface and should be interpreted
 * by {@link com.sf.ddao.conn.DaoInvocationHandlerImpl}.
 * <p/>
 * Created by: Pavel Syrtsov
 * Date: Apr 1, 2007
 * Time: 11:56:36 PM
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@CommandAnnotation(JNDIDataSourceHandler.class)
public @interface JNDIDao {
    /**
     * @return JNDI name of DataSource this Dao connected to
     */
    String value();
}
