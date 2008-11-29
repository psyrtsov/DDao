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

package com.sf.ddao.mapper;

import java.sql.ResultSet;

/**
 * Created by: Pavel Syrtsov
 * Date: Apr 15, 2007
 * Time: 2:35:11 PM
 */
public abstract class SingleRecordResultSetMapper implements ResultSetMapper {
    private Object result;

    public boolean addRecord(ResultSet resultSet) throws Exception {
        if (result != null) {
            // for this result set mapper only one row expected
            throw new ResultSetMapperException("Query returned more then one record");
        }
        this.result = mapRecord(resultSet);
        return true;
    }

    public abstract Object mapRecord(ResultSet resultSet) throws Exception;

    public Object getResult() {
        try {
            return result;
        } finally {
            result = null;
        }
    }
}
