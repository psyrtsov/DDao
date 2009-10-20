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

package com.sf.ddao.factory;

import com.sf.ddao.alinker.factory.ImplementedBy;
import com.sf.ddao.factory.param.StatementParameterException;

import java.lang.reflect.AnnotatedElement;
import java.sql.Connection;
import java.sql.PreparedStatement;

/**
 * Created by Pavel Syrtsov
 * Date: Nov 3, 2007
 * Time: 8:16:33 PM
 * <p/>
 * StatementFactory interface defines ability of the object to:
 * create prepared statment and bind all necessary parameters to it.
 * When this factory object created framework will initialize it with method setup
 * Then it will be used to create JDBC prepared statement
 * and bind to it arguments data objects if any.
 * To define what implementation class shall be used to construct statement for Dao
 * use annotation {@link com.sf.ddao.UseStatementFactory}
 */
@ImplementedBy(DefaultStatementFactory.class)
public interface StatementFactory {
    /**
     * Initializes factory object with query data
     *
     * @param sql    - query text
     * @param method - method that quey will be attached to
     * @throws StatementFactoryException - throws excpetion if fails to parse query
     */
    void init(AnnotatedElement element, String sql) throws StatementFactoryException;

    /**
     * Creates new PreparedStatement according to definition given in setup.
     * This method implementation has to be thread safe.
     *
     * @param connection - JDBC connection to be used to create new statement
     * @param args       - parameters values to bind to prepared statement
     * @return PreparedStatement instance ready to be executed
     * @throws StatementFactoryException if it fails to create
     *                                   and initialize prepared statement
     */
    PreparedStatement createStatement(Connection connection, Object[] args) throws StatementFactoryException;

    /**
     * Creates new PreparedStatement according to definition given in setup.
     * This method implementation has to be thread safe.
     *
     * @param connection          - JDBC connection to be used to create new statement
     * @param args                - parameters values to bind to prepared statement
     * @param returnGeneratedKeys - if not equal to Integer.MAX_VALUE then will be
     *                            passed to connection.createPreparedStatment
     * @return PreparedStatement instance ready to be executed
     * @throws StatementFactoryException if it fails to create
     *                                   and initialize prepared statement
     */
    PreparedStatement createStatement(Connection connection, Object[] args, int returnGeneratedKeys) throws StatementFactoryException;

    String createText(Object[] args) throws StatementParameterException;
}
