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

package com.syrtsov.ddao.mapper;

import java.sql.ResultSet;
import java.util.Collection;

/**
 * Created by: Pavel Syrtsov
 * Date: Apr 8, 2007
 * Time: 4:49:21 PM
 */
public class CollectionResultSetMapper extends ListResultSetMapper {
    private ResultSetMapper itemMapper;

    public CollectionResultSetMapper(ResultSetMapper itemMapper, Class<? extends Collection> returnType) {
        super(returnType);
        this.itemMapper = itemMapper;
    }

    public boolean addRecord(ResultSet resultSet) throws Exception {
        boolean res = itemMapper.addRecord(resultSet);
        Object o = itemMapper.getResult();
        addValue(o);
        return res;
    }
}
