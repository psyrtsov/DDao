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

import com.sf.ddao.DaoException;
import com.sf.ddao.SelectCallback;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.Converter;

import java.io.InputStream;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Blob;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Created by: Pavel Syrtsov
 * Date: Apr 14, 2007
 * Time: 5:01:14 PM
 */
public class ResultSetMapperRegistry {
    @SuppressWarnings({"UnusedDeclaration"})
    public static ResultSetMapper getResultSetMapper(Method method, Object[] args, ResultSet resultSet) throws ResultSetMapperException {
        Class<?> returnClass = method.getReturnType();
        Type returnType = method.getGenericReturnType();
        if (method.getReturnType() == Void.TYPE) {
            return createCallbackMapper(args);
        }
        if (returnClass.isArray()) {
            Class itemType = returnClass.getComponentType();
            ResultSetMapper itemMapper = getResultMapper(itemType);
            return new ArrayResultSetMapper(itemMapper, returnClass);
        }
        if (Collection.class.isAssignableFrom(returnClass)) {
            Type[] actualTypeArguments = ((ParameterizedType) returnType).getActualTypeArguments();
            Type itemType = actualTypeArguments[0];
            ResultSetMapper itemMapper = getResultMapper(itemType);
            //noinspection unchecked
            return new CollectionResultSetMapper(itemMapper, (Class<? extends List>) returnClass);
        }
        return getResultMapper(returnClass);
    }

    public static ResultSetMapper createCallbackMapper(Object[] args) throws ResultSetMapperException {
        for (Object arg : args) {
            if (arg instanceof SelectCallback) {
                final SelectCallback selectCallback = (SelectCallback) arg;
                Type[] ifaces = selectCallback.getClass().getGenericInterfaces();
                for (Type type : ifaces) {
                    if (type instanceof ParameterizedType && type.toString().startsWith(SelectCallback.class.getName())) {
                        Type[] actualTypeArguments = ((ParameterizedType) type).getActualTypeArguments();
                        Type itemType = actualTypeArguments[0];
                        final ResultSetMapper resultSetMapper = ResultSetMapperRegistry.getResultMapper(itemType);
                        return new ResultSetMapper() {
                            public boolean addRecord(ResultSet resultSet) throws Exception {
                                resultSetMapper.addRecord(resultSet);
                                Object result = resultSetMapper.getResult();
                                //noinspection unchecked
                                return selectCallback.processRecord(result);
                            }

                            public Object getResult() {
                                return null;
                            }
                        };
                    }
                }
            }
        }
        return new VoidResultSetMapper();
    }

    public static ResultSetMapper getResultMapper(Type itemType) {
        ColumnMapper columnMapper = getColumnMapper(itemType);
        if (columnMapper != null) {
            return new PrimitiveTypeResultSetMappper(itemType, columnMapper);
        }
        if (itemType instanceof Class) {
            Class itemClass = (Class) itemType;
            if (SelfMapping.class.isAssignableFrom(itemClass)) {
                //noinspection unchecked
                return new SelfMappingMapper(itemClass);
            }

            if (Map.class.isAssignableFrom(itemClass)) {
                return new MapResultSetMapper();
            }
            return new BeanResultSetMapper(itemClass);
        }
        throw new DaoException("No mapping defined for type " + itemType);
    }

    public static ColumnMapper getColumnMapper(final Type itemType) {
        if (itemType == String.class) {
            return new ColumnMapper() {
                public Object get(ResultSet rs, int idx) throws SQLException {
                    return rs.getString(idx);
                }
            };
        }
        if (itemType == Integer.class || itemType == Integer.TYPE) {
            return new ColumnMapper() {
                public Object get(ResultSet rs, int idx) throws SQLException {
                    return rs.getInt(idx);
                }
            };
        }
        if (itemType == URL.class) {
            return new ColumnMapper() {
                public Object get(ResultSet rs, int idx) throws SQLException {
                    return rs.getURL(idx);
                }
            };
        }
        if (itemType == BigDecimal.class) {
            return new ColumnMapper() {
                public Object get(ResultSet rs, int idx) throws SQLException {
                    return rs.getBigDecimal(idx);
                }
            };
        }
        if (itemType == InputStream.class) {
            return new ColumnMapper() {
                public Object get(ResultSet rs, int idx) throws SQLException {
                    return rs.getBinaryStream(idx);
                }
            };
        }
        if (itemType == Blob.class) {
            return new ColumnMapper() {
                public Object get(ResultSet rs, int idx) throws SQLException {
                    return rs.getBlob(idx);
                }
            };
        }
        if (itemType == java.sql.Date.class || itemType == java.util.Date.class) {
            return new ColumnMapper() {
                public Object get(ResultSet rs, int idx) throws SQLException {
                    return rs.getDate(idx);
                }
            };
        }
        if (itemType instanceof Class) {
            final Class itemClass = (Class) itemType;
            final Converter converter = ConvertUtils.lookup(itemClass);
            if (converter != null) {
                return new ColumnMapper() {
                    public Object get(ResultSet rs, int idx) throws SQLException {
                        String s = rs.getString(idx);
                        if (s == null) {
                            return null;
                        }
                        return converter.convert(itemClass, s);
                    }
                };
            }
        }
        return null;
    }
}
