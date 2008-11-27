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

import com.syrtsov.ddao.alinker.initializer.InitializerException;
import com.syrtsov.ddao.DaoException;
import com.syrtsov.ddao.SqlAnnotation;
import com.syrtsov.ddao.SqlOperation;
import com.syrtsov.ddao.handler.Intializible;
import com.syrtsov.ddao.utils.Annotations;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * psdo: add class comments
 * Created-By: Pavel Syrtsov
 * Date: Apr 10, 2008
 * Time: 9:39:39 PM
 */
public class DaoInvocationHandlerImpl implements Intializible, DaoInvocationHandler {
    private volatile Map<Method, SqlOperation> sqlOpMap = null;

    public Object invoke(Connection connection, Method method, Object[] args) throws Throwable {
        SqlOperation sqlOperation = getSqlOp(method);
        return sqlOperation.invoke(connection, method, args);
    }

    private SqlOperation getSqlOp(Method method) throws InitializerException {
        if (sqlOpMap == null) {
            init(method.getDeclaringClass());
        }
        SqlOperation sqlOperation = sqlOpMap.get(method);
        if (sqlOperation == null) {
            throw new DaoException("Method " + method + "has to have annotation annotated with " + SqlAnnotation.class);
        }
        return sqlOperation;
    }

    public void init(Class<?> iFace, Annotation annotation, List<Class<?>> iFaceList) throws InitializerException {
        if (!Boolean.getBoolean("LAZY_INIT")) {
            init(iFace);
        }
    }

    private synchronized void init(Class<?> iFace) throws InitializerException {
        final HashMap<Method, SqlOperation> res = new HashMap<Method, SqlOperation>();
        for (Method method : iFace.getMethods()) {
            SqlAnnotation sqlAnnotation = Annotations.findAnnotation(method, SqlAnnotation.class);
            if (sqlAnnotation == null) { // we'll skip it here , but will enforce it at execution time
                continue;
            }
            Class<? extends SqlOperation> clazz = sqlAnnotation.value();
            SqlOperation sqlOperation;
            try {
                sqlOperation = clazz.newInstance();
            } catch (Exception e) {
                throw new InitializerException("Failed to create sql operation handler for " + method, e);
            }
            sqlOperation.init(method);
            res.put(method, sqlOperation);
        }
        sqlOpMap = res;
    }
}
