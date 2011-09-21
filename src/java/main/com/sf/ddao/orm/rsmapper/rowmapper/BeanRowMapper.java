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

import com.sf.ddao.LoadAwareBean;
import com.sf.ddao.orm.RSMapperFactoryRegistry;
import com.sf.ddao.orm.RowMapper;

import java.beans.BeanInfo;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
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
 * Time: 6:12:20 PM
 */
public class BeanRowMapper implements RowMapper {
    private volatile List<PropertyMapper> mapperList = null;
    private Constructor constructor;
    private final Class itemType;

    private class PropertyMapper {
        private Method writeMethod;
        private RowMapper columnMapper;

        public PropertyMapper(String name, PropertyDescriptor propertyDescriptor) {
            writeMethod = propertyDescriptor.getWriteMethod();
            Class<?> propertyType = propertyDescriptor.getPropertyType();
            columnMapper = RSMapperFactoryRegistry.getScalarMapper(propertyType, name, true);
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
        if (constructor == null) {
            throw new RuntimeException("Type " + itemType + " has to have parameterless constructor");
        }
    }

    public Object map(ResultSet rs) throws SQLException {
        Object result;
        try {
            if (mapperList == null) {
                init(rs);
            }
            result = constructor.newInstance();
        } catch (SQLException e) {
            throw e;
        } catch (Exception e) {
            throw new SQLException(e);
        }
        for (PropertyMapper propertyMapper : mapperList) {
            try {
                propertyMapper.map(rs, result);
            } catch (Exception e) {
                throw new SQLException("Error mapping property " + propertyMapper.writeMethod, e);
            }
        }
        if (result instanceof LoadAwareBean) {
            LoadAwareBean loadAwareBean = (LoadAwareBean) result;
            loadAwareBean.beanIsLoaded();
        }
        return result;
    }

    private void init(ResultSet resultSet) throws Exception {
        ResultSetMetaData metaData = resultSet.getMetaData();
        Map<String, String> colNames = new HashMap<String, String>();
        int count = metaData.getColumnCount();
        for (int i = 1; i <= count; i++) {
            final String colName = metaData.getColumnName(i);
            String name = colName.toLowerCase();
            name = stripUnderscore(name);
            if (colNames.put(name, colName) != null) {
                throw new SQLException("Ambiguous column name " + name);
            }
        }
        List<PropertyMapper> mapperList = new ArrayList<PropertyMapper>(count);
        BeanInfo beanInfo = java.beans.Introspector.getBeanInfo(itemType);
        PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
        StringBuilder propList = new StringBuilder();
        for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
            if (propertyDescriptor.getWriteMethod() == null) {
                propList.append(propertyDescriptor.getName()).append(" - no writer\n");
                continue;
            }
            String name = propertyDescriptor.getName();
            name = name.toLowerCase();
            String colName = colNames.remove(name);
            if (colName == null) {
                propList.append(propertyDescriptor.getName()).append(" - no matching column\n");
                continue;
            } else {
                propList.append(propertyDescriptor.getName()).append(" - found matching column\n");
            }
            mapperList.add(new PropertyMapper(colName, propertyDescriptor));
        }
        if (colNames.size() > 0) {
            throw new SQLException("Query result columns " + colNames.values()
                    + " don`t have matching properties in " + itemType
                    + (propList.length() > 0 ? ", list of existing writable properties:\n" + propList : ""));
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
