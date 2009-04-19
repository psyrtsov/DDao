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

package com.sf.ddao.bean.impl;

import com.sf.ddao.SqlOperation;
import com.sf.ddao.alinker.initializer.InitializerException;

import java.lang.reflect.Method;
import java.sql.Connection;

/**
 * Created by: Pavel Syrtsov
 * Date: Apr 18, 2009
 * Time: 5:16:15 PM
 */
public class BeanInsertOperation extends BeanSqlHelper implements SqlOperation {
    @Override
    public Object invoke(Connection connection, Method method, Object[] args) {
        //psdo: review generated code

        return null;
    }

    @Override
    public void init(Method method) throws InitializerException {
        final Class<?>[] parameterTypes = method.getParameterTypes();
        Class<?> beanType = parameterTypes[0];
        String tableName = getTableName(beanType);
    }

    public static String getTableName(Class<?> beanType) {
        String className = beanType.getSimpleName();
        StringBuilder sb = new StringBuilder();
        sb.append(Character.toLowerCase(className.charAt(0)));
        for(int i=1; i<className.length();i++) {
            char ch = className.charAt(i);
            if (Character.isUpperCase(ch)) {
                ch = Character.toLowerCase(ch);
                sb.append("_");
            }
            sb.append(ch);
        }
        return sb.toString();
    }
}
