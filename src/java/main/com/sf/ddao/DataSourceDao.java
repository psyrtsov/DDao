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

package com.sf.ddao;

import com.sf.ddao.alinker.factory.UseFactory;
import com.sf.ddao.chain.ChainInvocationHandler;
import com.sf.ddao.chain.ChainMember;
import com.sf.ddao.conn.DataSourceHandler;
import com.sf.ddao.handler.InvocationHandlerAnnotation;
import com.sf.ddao.handler.InvocationHandlerFactory;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This dao type is created for scenario when we want to use DataSources but without dealing
 * with complexity of setting up JNDI in J2SE JVM.
 * It uses static hash map of DataSources maintained in DataSourceHandler class
 * Created by: Pavel Syrtsov
 * Date: Apr 1, 2007
 * Time: 11:56:36 PM
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@UseFactory(InvocationHandlerFactory.class)
@InvocationHandlerAnnotation(value = ChainInvocationHandler.class, singleton = true)
@ChainMember(DataSourceHandler.class)
public @interface DataSourceDao {
    /**
     * @return name of datasource that should match to the key in static hash map in DataSourceHandler class
     */
    String value();
}
