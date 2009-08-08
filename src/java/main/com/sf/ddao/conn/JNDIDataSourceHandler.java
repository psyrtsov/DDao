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

package com.sf.ddao.conn;

import com.sf.ddao.DaoException;
import com.sf.ddao.JNDIDao;
import com.sf.ddao.alinker.initializer.InitializerException;
import com.sf.ddao.handler.Intializible;

import javax.naming.InitialContext;
import javax.sql.DataSource;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Hashtable;

/**
 * psdo: add class comments
 * Created-By: Pavel Syrtsov
 * Date: Apr 10, 2008
 * Time: 9:39:39 PM
 */
public class JNDIDataSourceHandler extends ConnectionHandlerHelper implements Intializible {
    public static String DS_CTX_PREFIX = "java:comp/env/";
    private DataSource dataSource;

    public Connection createConnection(Method method, Object[] args) throws SQLException {
        return dataSource.getConnection();
    }

    public void init(AnnotatedElement element, Annotation annotation) throws InitializerException {
        JNDIDao daoAnnotation = (JNDIDao) annotation;
        String dsName = daoAnnotation.value();
        try {
            InitialContext ic = new InitialContext(new Hashtable());
            dataSource = (DataSource) ic.lookup(DS_CTX_PREFIX + dsName);
        } catch (Exception e) {
            throw new DaoException("Failed to find DataSource " + dsName, e);
        }
        super.init(element, annotation);
    }
}
