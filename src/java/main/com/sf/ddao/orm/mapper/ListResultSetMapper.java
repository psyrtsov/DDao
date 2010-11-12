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

package com.sf.ddao.orm.mapper;

import com.sf.ddao.orm.ResultSetMapper;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by: Pavel Syrtsov
 * Date: Apr 8, 2007
 * Time: 4:49:21 PM
 */
public abstract class ListResultSetMapper implements ResultSetMapper {
    private Collection list;

    public ListResultSetMapper() {
        this(Collection.class);
    }

    public ListResultSetMapper(Class<? extends Collection> returnType) {
        try {
            if (returnType.isInterface() &&
                    returnType.isAssignableFrom(ArrayList.class)) {
                list = new ArrayList();
            } else {
                list = returnType.newInstance();
            }
        } catch (Exception e) {
            throw new RuntimeException("", e);
        }
    }

    public void addValue(Object o) {
        //noinspection unchecked
        list.add(o);
    }

    public Object getResult() {
        try {
            return list;
        } finally {
            list = null;
        }
    }


    public Collection getList() {
        return list;
    }
}
