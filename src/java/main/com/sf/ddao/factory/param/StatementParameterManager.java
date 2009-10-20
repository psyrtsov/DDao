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

import java.lang.reflect.AnnotatedElement;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created-By: Pavel Syrtsov
 * Date: Apr 10, 2008
 * Time: 3:29:23 PM
 */
public class StatementParameterManager {
    static Map<String, Class<? extends StatementParameter>> paramTypeMap = new HashMap<String, Class<? extends StatementParameter>>();

    static {
        paramTypeMap.put(ThreadLocalStatementParameter.FUNC_NAME, ThreadLocalStatementParameter.class);
    }

    public static StatementParameter createStatementParameter(AnnotatedElement element, String name) throws StatementParameterException {
        StatementParameter param;
        try {
            final int openBracketIdx = name.indexOf('(');
            if (openBracketIdx > 0) {
                String factoryName = name.substring(0, openBracketIdx);
                final Class<? extends StatementParameter> aClass = paramTypeMap.get(factoryName);
                param = aClass.newInstance();
            } else {
                param = new StatementParameterImpl();
            }
            param.init(element, name);
        } catch (Exception e) {
            throw new StatementParameterException("Failed to create statement parameter '" + name + "'");
        }
        return param;
    }

    public static Class<? extends StatementParameter> register(String funcName, Class<? extends StatementParameter> paramClass) {
        return paramTypeMap.put(funcName, paramClass);
    }

    public static void register(Properties props) throws ClassNotFoundException {
        for (Object key : props.keySet()) {
            String funcName = key.toString();
            String className = props.get(key).toString();
            //noinspection unchecked
            Class<? extends StatementParameter> clazz = (Class<? extends StatementParameter>) Class.forName(className);
            register(funcName, clazz);
        }
    }

    public static Class<? extends StatementParameter> remove(String funcName) {
        return paramTypeMap.remove(funcName);
    }

}
