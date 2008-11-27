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

import com.syrtsov.ddao.alinker.factory.UseFactory;
import com.syrtsov.ddao.conn.JNDIDataSourceHandler;
import com.syrtsov.ddao.handler.HandlerAnnotation;
import com.syrtsov.ddao.handler.HandlerFactory;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * psdo: comments
 * This annotation defines tnat interface logic that it is attached to
 * is Dao interface and should be interpreted
 * by {@link com.syrtsov.ddao.conn.DaoInvocationHandlerImpl}.
 * <p/>
 * Created by: Pavel Syrtsov
 * Date: Apr 1, 2007
 * Time: 11:56:36 PM
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@HandlerAnnotation(JNDIDataSourceHandler.class)
@UseFactory(HandlerFactory.class)
public @interface JNDIDao {
    /**
     * @return JNDI name of DataSource this Dao connected to
     */
    String value();
}
