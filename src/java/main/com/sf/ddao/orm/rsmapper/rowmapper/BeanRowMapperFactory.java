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

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;

/**
 * Created by: Pavel Syrtsov
 * Date: Apr 8, 2007
 * Time: 6:12:20 PM
 */
public class BeanRowMapperFactory implements RowMapperFactory {
    private Constructor constructor;
    private Map<String, PropertyDescriptor> props;

    private static class PropertyMapper {
        private Method writeMethod;
        private RowMapper columnMapper;

        public PropertyMapper(String name, PropertyDescriptor propertyDescriptor) {
            writeMethod = propertyDescriptor.getWriteMethod();
            Class<?> propertyType = propertyDescriptor.getPropertyType();
            columnMapper = RSMapperFactoryRegistry.getScalarRowMapperFactory(propertyType, name, true).get();
        }

        public void map(ResultSet resultSet, Object result) throws Exception {
            Object value = columnMapper.map(resultSet);
            writeMethod.invoke(result, value);
        }

        @Override
        public String toString() {
            return writeMethod.toString();
        }
    }

    public RowMapper get() {
        return new BeanRowMapper();
    }


    public BeanRowMapperFactory(Class itemType) {
        for (Constructor c : itemType.getDeclaredConstructors()) {
            if (c.getParameterTypes().length == 0) {
                c.setAccessible(true);
                constructor = c;
            }
        }
        if (constructor == null) {
            throw new RuntimeException("Type " + itemType + " has to have parameterless constructor");
        }
        BeanInfo beanInfo;
        try {
            beanInfo = java.beans.Introspector.getBeanInfo(itemType);
        } catch (IntrospectionException e) {
            throw new RuntimeException(e);
        }
        props = new HashMap<String, PropertyDescriptor>();
        PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
        for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
            if (propertyDescriptor.getWriteMethod() == null) {
                continue;
            }
            String name = propertyDescriptor.getName();
            name = name.toLowerCase();
            props.put(name, propertyDescriptor);
        }
    }

    private class BeanRowMapper implements RowMapper {
        private List<PropertyMapper> mappers = null;

        public Object map(ResultSet rs) throws SQLException {
            Object result;
            try {
                if (mappers == null) {
                    init(rs);
                }
                result = constructor.newInstance();
            } catch (SQLException e) {
                throw e;
            } catch (Exception e) {
                throw new SQLException(e);
            }
            for (PropertyMapper propertyMapper : mappers) {
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

        private synchronized void init(ResultSet resultSet) throws Exception {
            ResultSetMetaData metaData = resultSet.getMetaData();
            int count = metaData.getColumnCount();
            List<PropertyMapper> mappers = new ArrayList<PropertyMapper>(count);
            Set<String> colNames = new HashSet<String>();
            for (int i = 1; i <= count; i++) {
                final String colName = metaData.getColumnName(i);
                String name = colName.toLowerCase();
                name = stripUnderscore(name);
                if (!colNames.add(name)) {
                    throw new SQLException("Ambiguous column name " + colName + "(" + colNames + ")");
                }
                PropertyDescriptor propertyDescriptor = props.get(name);
                if (propertyDescriptor == null) {
                    throw new SQLException("Column " + colName + " doesn't have matching property");
                }
                mappers.add(new PropertyMapper(colName, propertyDescriptor));
            }
            this.mappers = mappers;
        }

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
