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

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by: Pavel Syrtsov
 * Date: Apr 8, 2007
 * Time: 6:10:40 PM
 */
public class MapResultSetMapper implements ResultSetMapper {
    Map<String, Object> data = new HashMap<String, Object>();
    List<String> columns = null;

    public MapResultSetMapper() {
    }

    public boolean addRecord(ResultSet resultSet) throws Exception {
        if (columns == null) {
            columns = new ArrayList<String>();
            final ResultSetMetaData metaData = resultSet.getMetaData();
            int colCount = metaData.getColumnCount();
            for (int i = 1; i <= colCount; i++) {
                String colName = metaData.getColumnName(i);
                columns.add(colName);
            }
        }
        for (int i = 0; i < columns.size(); i++) {
            String column = columns.get(i);
            Object value = resultSet.getObject(i + 1);
            data.put(column, value);
        }
        return true;
    }

    public Object getResult() {
        Map<String, Object> res = data;
        data = new HashMap<String, Object>();
        return res;
    }
}
