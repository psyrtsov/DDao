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

import com.syrtsov.alinker.factory.UseFactory;
import com.syrtsov.ddao.conn.JDBCConnectionHandler;
import com.syrtsov.handler.HandlerAnnotation;
import com.syrtsov.handler.HandlerFactory;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * psdo: comments
 * Created by: Pavel Syrtsov
 * Date: Apr 1, 2007
 * Time: 11:56:36 PM
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@HandlerAnnotation(JDBCConnectionHandler.class)
@UseFactory(HandlerFactory.class)
public @interface JDBCDao {
    /**
     * @return connection URL
     */
    String value();

    String driver() default "";

    String user() default "";

    String pwd() default "";
}