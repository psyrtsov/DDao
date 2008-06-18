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

package com.syrtsov.ddao.conn;

import com.syrtsov.alinker.initializer.InitializerException;
import com.syrtsov.ddao.DaoException;
import com.syrtsov.ddao.JDBCDao;
import com.syrtsov.handler.Intializible;

import java.lang.annotation.Annotation;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

/**
 * psdo: add class comments
 * Created-By: Pavel Syrtsov
 * Date: Apr 10, 2008
 * Time: 9:39:39 PM
 */
public class JDBCConnectionHandler extends ConnectionHandlerHelper implements Intializible {
    private JDBCDao jdbcDao;

    protected Connection createConnection() throws SQLException {
        Connection res;
        if (jdbcDao.user() == null || jdbcDao.user().length() == 0) {
            res = DriverManager.getConnection(jdbcDao.value());
        } else {
            res = DriverManager.getConnection(jdbcDao.value(), jdbcDao.user(), jdbcDao.pwd());
        }
        return res;
    }


    public void init(Class<?> iFace, Annotation annotation, List<Class<?>> iFaceList) throws InitializerException {
        jdbcDao = (JDBCDao) annotation;
        if (jdbcDao.driver() != null && jdbcDao.driver().length() > 0) {
            try {
                Class.forName(jdbcDao.driver());
            } catch (Exception e) {
                throw new DaoException("Failed to load driver " + jdbcDao.driver(), e);
            }
        }
        super.init(iFace, annotation, iFaceList);
    }
}