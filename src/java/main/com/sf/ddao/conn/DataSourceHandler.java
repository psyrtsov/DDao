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

import com.sf.ddao.DataSourceDao;
import com.sf.ddao.alinker.initializer.InitializerException;
import com.sf.ddao.handler.Intializible;
import org.apache.commons.chain.Context;

import javax.sql.DataSource;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * This ConnectionHandler is using static hash map to get DataSource object,
 * can be helpful when we want flexibility of defining data source parameters from some config file
 * but don't want to deal with JNDI
 * <p/>
 * Created-By: Pavel Syrtsov
 * Date: Apr 10, 2008
 * Time: 9:39:39 PM
 */
public class DataSourceHandler extends ConnectionHandlerHelper implements Intializible {
    public static final ConcurrentMap<String, DataSource> dataSourceMap = new ConcurrentHashMap<String, DataSource>();
    private String dsName;

    public void init(AnnotatedElement element, Annotation annotation) throws InitializerException {
        super.init(element, annotation);
        DataSourceDao daoAnnotation = (DataSourceDao) annotation;
        dsName = daoAnnotation.value();
    }

    @Override
    public Connection createConnection(Context context) throws SQLException {
        DataSource dataSource = dataSourceMap.get(dsName);
        if (dataSource == null) {
            throw new NullPointerException("DataSource with name " + dsName + " should be regstered at " + DataSourceHandler.class);
        }
        return dataSource.getConnection();
    }
}