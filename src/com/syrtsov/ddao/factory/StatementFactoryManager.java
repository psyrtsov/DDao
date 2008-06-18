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

package com.syrtsov.ddao.factory;

import com.syrtsov.ddao.UseStatementFactory;

import java.lang.reflect.Method;

/**
 * StatementFactoryManager maintains lazy loaded cache of mappings of method objects
 * to StatementFactory implementations.
 * <p/>
 * Created by Pavel Syrtsov
 * Date: Nov 3, 2007
 * Time: 5:11:13 PM
 */
public class StatementFactoryManager {
    /**
     * default implementation class
     */
    public static Class<? extends StatementFactory> defaultStatementFactory = DefaultStatementFactory.class;

    public static StatementFactory createStatementFactory(Method method, String sql) throws StatementFactoryException {
        Class<? extends StatementFactory> statementFactoryClass = getFactoryClass(method);
        StatementFactory sf;
        try {
            // psdo: this should be replaced with call to aLinker
            sf = statementFactoryClass.newInstance();
        } catch (Exception e) {
            throw new StatementFactoryException("Failed to create statement factory for " + method);
        }
        sf.init(sql, method);
        return sf;
    }

    private static Class<? extends StatementFactory> getFactoryClass(Method method) {
        UseStatementFactory useStatementFactory = method.getAnnotation(UseStatementFactory.class);
        if (useStatementFactory != null) {
            return useStatementFactory.value();
        }
        Class<?> declaringClass = method.getDeclaringClass();
        useStatementFactory = declaringClass.getAnnotation(UseStatementFactory.class);
        if (useStatementFactory != null) {
            return useStatementFactory.value();
        }
        return defaultStatementFactory;
    }
}
