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

import com.syrtsov.ddao.alinker.initializer.InitializerException;

import java.lang.reflect.Method;
import java.sql.Connection;

/**
 * psdo: comments
 * SqlOperation defines ability to execute SQL operation.
 * Class that implements this interface has to be associated with corresponding
 * annotation using
 * {@link com.syrtsov.ddao.SqlAnnotation}.
 * When DDao framework finds annotation that associates interface method with class implementing this           
 * interface it will instantiate that class and will initialize it with method information
 * <p/>
 * Created by: Pavel Syrtsov
 * Date: Apr 2, 2007
 * Time: 8:01:41 PM
 */
public interface SqlOperation {
    /**
     * this method executes SQL operation and returns result mapped to java object
     *
     * @param connection - JDBC connection that should be used to execute operation
     * @param method     - Dao method object associated with this operation invocation
     * @param args       - argument array given with Dao method invocation
     * @return result of query execution mapped to java object
     */
    Object invoke(Connection connection, Method method, Object[] args);

    /**
     * initialize instance with data defined by annotations attached to given method
     * @param method - method that should be used to initialize this operation,
     * usually we use annotation attached to this method and that annotation has SQL
     * @throws InitializerException - thrown when given method can not be used to
     * initialize this operation
     */
    void init(Method method) throws InitializerException;
}
