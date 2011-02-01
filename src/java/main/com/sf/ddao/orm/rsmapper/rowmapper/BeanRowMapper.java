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

import com.sf.ddao.orm.RSMapperFactoryRegistry;
import com.sf.ddao.orm.RowMapper;

import java.beans.BeanInfo;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by: Pavel Syrtsov
 * Date: Apr 8, 2007
 * Time: 6:12:20 PM
 */
public class BeanRowMapper implements RowMapper {
    private volatile PropertyMapper[] mapperList = null;
    private Constructor constructor;
    private final Class itemType;

    private class PropertyMapper {
        private Method writeMethod;
        private RowMapper columnMapper;

        public PropertyMapper(int idx, PropertyDescriptor propertyDescriptor) {
            writeMethod = propertyDescriptor.getWriteMethod();
            Class<?> propertyType = propertyDescriptor.getPropertyType();
            columnMapper = RSMapperFactoryRegistry.getScalarMapper(propertyType, idx);
            if (columnMapper == null) {
                throw new IllegalArgumentException("no mapping defined for " + propertyType);
            }
        }

        public void map(ResultSet resultSet, Object result) throws Exception {
            Object value = columnMapper.map(resultSet);
            writeMethod.invoke(result, value);
        }
    }

    public BeanRowMapper(Class itemType) {
        this.itemType = itemType;
        for (Constructor c : itemType.getDeclaredConstructors()) {
            if (c.getParameterTypes().length == 0) {
                c.setAccessible(true);
                constructor = c;
            }
        }
    }

    public Object map(ResultSet rs) throws SQLException {
        Object result;
        try {

            if (mapperList == null) {
                init(rs);
            }
            result = constructor.newInstance();
            for (PropertyMapper propertyMapper : mapperList) {
                propertyMapper.map(rs, result);
            }
        } catch (SQLException e) {
            throw e;
        } catch (Exception e) {
            throw new SQLException(e);
        }
        return result;
    }

    private synchronized void init(ResultSet resultSet) throws Exception {
        if (this.mapperList != null) {
            return;
        }
        ResultSetMetaData metaData = resultSet.getMetaData();
        Map<String, Integer> colNames = new HashMap<String, Integer>();
        int count = metaData.getColumnCount();
        for (int i = 1; i <= count; i++) {
            String name = metaData.getColumnName(i).toLowerCase();
            name = stripUnderscore(name);
            if (colNames.put(name, i) != null) {
                throw new SQLException("Ambiguous column name " + name);
            }
        }
        PropertyMapper[] mapperList = new PropertyMapper[count];
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
            throw new SQLException("Query result columns " + colNames.keySet()
                    + " don`t have matching properties in " + itemType);
        }
        this.mapperList = mapperList;
    }

    private static String stripUnderscore(String name) {
        StringBuilder sb = new StringBuilder(name.length());
        for (char ch : name.toCharArray()) {
            if (ch != '_') {
                sb.append(ch);
            }
        }
        return sb.toString();
    }
}
