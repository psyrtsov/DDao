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

package com.syrtsov.ddao.ops;

import com.syrtsov.alinker.initializer.InitializerException;
import com.syrtsov.ddao.Insert;

import java.lang.reflect.Method;

/**
 * Created by: Pavel Syrtsov
 * Date: Apr 24, 2007
 * Time: 1:43:33 AM
 */
public class InsertSqlOperation extends UpdateSqlOperation {
    public void init(Method method) throws InitializerException {
        Insert insertAnnotation = method.getAnnotation(Insert.class);
        String sql = insertAnnotation.value();
        init(method, sql);
    }
}
