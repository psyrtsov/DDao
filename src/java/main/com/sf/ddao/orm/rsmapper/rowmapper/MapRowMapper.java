/*
 * Copyright 2008 Pavel Syrtsov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and limitations
 *  under the License.
 */

package com.sf.ddao.orm.rsmapper.rowmapper;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by: Pavel Syrtsov
 * Date: Apr 8, 2007
 * Time: 6:10:40 PM
 */
public class MapRowMapper implements RowMapper {
    private volatile List<String> columns = null;

    public MapRowMapper() {
    }

    public Object map(ResultSet rs) throws SQLException {
        if (columns == null) {
            initColumns(rs);
        }
        Map<String, Object> data = new HashMap<String, Object>();
        for (int i = 0; i < columns.size(); i++) {
            String column = columns.get(i);
            Object value = rs.getObject(i + 1);
            data.put(column, value);
        }
        return data;
    }

    private synchronized void initColumns(ResultSet rs) throws SQLException {
        if (columns != null) {
            return;
        }
        final ResultSetMetaData metaData = rs.getMetaData();
        int colCount = metaData.getColumnCount();
        List<String> columns = new ArrayList<String>(colCount);
        for (int i = 1; i <= colCount; i++) {
            String colName = metaData.getColumnName(i);
            columns.add(colName);
        }
        this.columns = columns;
    }
}
