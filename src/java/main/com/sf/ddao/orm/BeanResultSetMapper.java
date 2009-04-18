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

package com.sf.ddao.orm;

import java.beans.BeanInfo;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by: Pavel Syrtsov
 * Date: Apr 8, 2007
 * Time: 6:12:20 PM
 */
public class BeanResultSetMapper implements ResultSetMapper {
    private Class itemType;
    private Object result = null;
    private PropertyMapper[] mapperList = null;

    private class PropertyMapper {
        private Method writeMethod;
        private ColumnMapper columnMapper;
        private int idx;

        public PropertyMapper(int i, PropertyDescriptor propertyDescriptor) {
            writeMethod = propertyDescriptor.getWriteMethod();
            Class<?> propertyType = propertyDescriptor.getPropertyType();
            columnMapper = ResultSetMapperRegistry.getColumnMapper(propertyType);
            idx = i;
        }

        public void map(ResultSet resultSet, Object result) throws Exception {
            Object value = columnMapper.get(resultSet, idx);
            writeMethod.invoke(result, value);
        }
    }

    public BeanResultSetMapper(Class itemType) {
        this.itemType = itemType;
    }

    public boolean addRecord(ResultSet resultSet) throws Exception {
        if (mapperList == null) {
            init(resultSet);
        }
        if (result != null) {
            throw new ResultSetMapperException("Expected only one record for result type " + itemType);
        }
        result = itemType.newInstance();
        for (PropertyMapper propertyMapper : mapperList) {
            propertyMapper.map(resultSet, result);
        }
        return true;
    }

    private void init(ResultSet resultSet) throws Exception {
        ResultSetMetaData metaData = resultSet.getMetaData();
        Map<String, Integer> colNames = new HashMap<String, Integer>();
        int count = metaData.getColumnCount();
        for (int i = 1; i <= count; i++) {
            colNames.put(metaData.getColumnName(i).toLowerCase(), i);
        }
        mapperList = new PropertyMapper[count];
        BeanInfo beanInfo = java.beans.Introspector.getBeanInfo(itemType);
        PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
        for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
            if (propertyDescriptor.getWriteMethod() == null) {
                continue;
            }
            String name = propertyDescriptor.getName();
            name = name.toLowerCase();
            Integer i = colNames.remove(name);
            if (i == null) {
                continue;
            }
            mapperList[i - 1] = new PropertyMapper(i, propertyDescriptor);
        }
        if (colNames.size() > 0) {
            throw new ResultSetMapperException("Query result columns " + colNames.keySet()
                    + " don`t have matching properties in " + itemType);
        }
    }

    public Object getResult() {
        try {
            return result;
        } finally {
            result = null;
        }
    }
}
