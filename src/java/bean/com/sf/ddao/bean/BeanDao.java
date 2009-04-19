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

package com.sf.ddao.bean;

import java.util.ArrayList;

/**
 * Created by: Pavel Syrtsov
 * Date: Apr 18, 2009
 * Time: 5:06:34 PM
 */
public interface BeanDao<T> {
    @BeanInsert()
    long insert(T bean, String ... fieldNames);
    long update(T bean, String ... fieldNames);
    T get(long id);
    ArrayList<T> list(String where, Object ... params);
    int delete(long id);
}
