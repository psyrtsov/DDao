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

package com.sf.ddao.factory.param;

import java.lang.reflect.Method;
import java.sql.PreparedStatement;

/**
 * StatementParameter defines ability of object to extract value from method argument list
 * that can be passed as parameter to prepared statement.
 * <p/>
 * Created by Pavel Syrtsov
 * Date: Nov 27, 2007
 * Time: 7:07:45 PM
 */
public interface StatementParameter {
    /**
     * psdo: add comments
     *
     * @param method
     * @param name
     */
    void init(Method method, String name);

    /**
     * this method will be invoked by statement factory to get string presentation of
     * parameter that will be inlined in query text,
     *
     * @param args - method invocation argument list
     * @return value extracted from argument list
     * @throws StatementParameterException - thrown when failed to extract parameter
     */
    String extractParam(Object[] args) throws StatementParameterException;

    /**
     * bind parameter extracted from argument list to given prepared statement
     *
     * @param preparedStatement - prepared statement that has to be bound with parameter
     * @param idx               - index of parameter that should be bound,
     *                          should be used as second argument for PreparedStatement.setXXX
     * @param args              - method invocation argument list
     * @throws StatementParameterException - thrown when failed to bind parameter
     */
    void bind(PreparedStatement preparedStatement, int idx, Object[] args) throws StatementParameterException;
}
