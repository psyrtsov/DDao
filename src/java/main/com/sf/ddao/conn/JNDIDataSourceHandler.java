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

package com.sf.ddao.conn;

import com.sf.ddao.JNDIDao;
import com.sf.ddao.chain.InitializerException;
import com.sf.ddao.chain.Intializible;
import org.apache.commons.chain.Context;

import javax.naming.InitialContext;
import javax.sql.DataSource;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Hashtable;

/**
 * Created-By: Pavel Syrtsov
 * Date: Apr 10, 2008
 * Time: 9:39:39 PM
 */
public class JNDIDataSourceHandler extends ConnectionHandlerHelper implements Intializible {
    public static String DS_CTX_PREFIX = "java:comp/env/";
    private DataSource dataSource;

    public void init(AnnotatedElement element, Annotation annotation) {
        JNDIDao daoAnnotation = (JNDIDao) annotation;
        String dsName = daoAnnotation.value();
        try {
            InitialContext ic = new InitialContext(new Hashtable());
            dataSource = (DataSource) ic.lookup(DS_CTX_PREFIX + dsName);
        } catch (Exception e) {
            throw new InitializerException("Failed to find DataSource " + dsName, e);
        }
    }

    @Override
    public Connection createConnection(Context context) throws SQLException {
        return dataSource.getConnection();
    }
}
