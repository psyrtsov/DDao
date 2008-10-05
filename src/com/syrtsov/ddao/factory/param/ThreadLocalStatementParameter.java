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

package com.syrtsov.ddao.factory.param;

import java.util.HashMap;
import java.util.Map;

/**
 * Created-By: Pavel Syrtsov
 * Date: Apr 10, 2008
 * Time: 4:14:54 PM
 */
public class ThreadLocalStatementParameter extends StatementParameterHelper {
    private static ThreadLocal<Map<String, Object>> data = new ThreadLocal<Map<String, Object>>();
    public static final String FUNC_NAME = "global";

    @Override
    public Object extractData(Object[] args) throws StatementParameterException {
        final Map<String, Object> dataMap = data.get();
        if (dataMap == null) {
            return null;
        }
        return dataMap.get(name);
    }

    public static Object put(String key, Object value) {
        key = FUNC_NAME + "(" + key + ")";
        Map<String, Object> dataMap = data.get();
        if (dataMap == null) {
            dataMap = new HashMap<String, Object>();
            data.set(dataMap);
        }
        return dataMap.put(key, value);
    }

    public static Object remove(String key) {
        Map<String, Object> dataMap = data.get();
        if (dataMap == null) {
            return null;
        }
        return dataMap.remove(key);
    }

    public void clear() {
        data.remove();
    }
}
