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

package com.sf.ddao.orm;

import com.sf.ddao.DaoException;
import com.sf.ddao.orm.rsmapper.ArrayRSMapper;
import com.sf.ddao.orm.rsmapper.CollectionRSMapper;
import com.sf.ddao.orm.rsmapper.MapRSMapper;
import com.sf.ddao.orm.rsmapper.SingleRowRSMapper;
import com.sf.ddao.orm.rsmapper.rowmapper.BeanRowMapper;
import com.sf.ddao.orm.rsmapper.rowmapper.MapRowMapper;
import com.sf.ddao.orm.rsmapper.rowmapper.SelfRowMapper;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.Converter;

import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Blob;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Map;

/**
 * Created by: Pavel Syrtsov
 * Date: Apr 14, 2007
 * Time: 5:01:14 PM
 */
public class RSMapperFactoryRegistry {
    @SuppressWarnings({"UnusedDeclaration"})
    public static RSMapperFactory create(Method method) {
        int paramIdx = findParameter(method);
        if (paramIdx >= 0) {
            return new ParameterBasedRSMapperFactory(paramIdx);
        }
        final RSMapper single = createReusable(method);
        return new ReusableRSMapperFactory(single);
    }

    public static RSMapper createReusable(Method method) {
        final UseRSMapper useRSMapper = method.getAnnotation(UseRSMapper.class);
        if (useRSMapper != null) {
            try {
                return useRSMapper.value().newInstance();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        Class<?> returnClass = method.getReturnType();
        if (returnClass.isArray()) {
            Class itemType = returnClass.getComponentType();
            RowMapper rowMapper = getRowMapper(itemType);
            return new ArrayRSMapper(rowMapper, itemType);
        }
        Type returnType = method.getGenericReturnType();
        if (Collection.class.isAssignableFrom(returnClass)) {
            Type[] actualTypeArguments = ((ParameterizedType) returnType).getActualTypeArguments();
            Type itemType = actualTypeArguments[0];
            return getCollectionORMapper(itemType);
        }
        if (Map.class.isAssignableFrom(returnClass)) {
            Type[] actualTypeArguments = ((ParameterizedType) returnType).getActualTypeArguments();
            Type keyType = actualTypeArguments[0];
            Type valueType = actualTypeArguments[1];
            RowMapper keyMapper = getScalarMapper(keyType, 1);
            RowMapper valueMapper = getRowMapper(valueType);
            return new MapRSMapper(keyMapper, valueMapper);
        }
        return new SingleRowRSMapper(getRowMapper(returnType));
    }

    public static int findParameter(Method method) {
        final Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        for (int i = 0, parameterAnnotationsLength = parameterAnnotations.length; i < parameterAnnotationsLength; i++) {
            Annotation[] annotations = parameterAnnotations[i];
            for (Annotation annotation : annotations) {
                if (annotation.annotationType().isAssignableFrom(UseRSMapper.class)) {
                    return i;
                }
            }
        }
        return -1;
    }

    private static RSMapper getCollectionORMapper(Type itemType) {
        RowMapper rowMapper = getRowMapper(itemType);
        //noinspection unchecked
        return new CollectionRSMapper(rowMapper);
    }

    public static RowMapper getRowMapper(Type itemType) {
        // see if return type is simple so that we should map just first column 
        RowMapper scalarMapper = getScalarMapper(itemType, 1);
        if (scalarMapper != null) {
            return scalarMapper;
        }
        if (itemType instanceof Class) {
            Class itemClass = (Class) itemType;
            if (RowMapper.class.isAssignableFrom(itemClass)) {
                //noinspection unchecked
                return new SelfRowMapper(itemClass);
            }

            if (Map.class.isAssignableFrom(itemClass)) {
                return new MapRowMapper();
            }
            return new BeanRowMapper(itemClass);
        }
        throw new DaoException("No mapping defined for type " + itemType);
    }

    public static RowMapper getScalarMapper(final Type itemType, final int idx) {
        if (itemType == String.class) {
            return new RowMapper() {
                public Object map(ResultSet rs) throws SQLException {
                    return rs.getString(idx);
                }
            };
        }
        if (itemType == Integer.class || itemType == Integer.TYPE) {
            return new RowMapper() {
                public Object map(ResultSet rs) throws SQLException {
                    return rs.getInt(idx);
                }
            };
        }
        if (itemType == URL.class) {
            return new RowMapper() {
                public Object map(ResultSet rs) throws SQLException {
                    return rs.getURL(idx);
                }
            };
        }
        if (itemType == BigDecimal.class) {
            return new RowMapper() {
                public Object map(ResultSet rs) throws SQLException {
                    return rs.getBigDecimal(idx);
                }
            };
        }
        if (itemType == InputStream.class) {
            return new RowMapper() {
                public Object map(ResultSet rs) throws SQLException {
                    return rs.getBinaryStream(idx);
                }
            };
        }
        if (itemType == Boolean.class || itemType == Boolean.TYPE) {
            return new RowMapper() {
                public Object map(ResultSet rs) throws SQLException {
                    return rs.getBoolean(idx);
                }
            };
        }
        if (itemType == Blob.class) {
            return new RowMapper() {
                public Object map(ResultSet rs) throws SQLException {
                    return rs.getBlob(idx);
                }
            };
        }
        if (itemType == java.sql.Date.class || itemType == java.util.Date.class) {
            return new RowMapper() {
                public Object map(ResultSet rs) throws SQLException {
                    return rs.getTimestamp(idx);
                }
            };
        }
        if (itemType instanceof Class) {
            final Class itemClass = (Class) itemType;
            final Converter converter = ConvertUtils.lookup(itemClass);
            if (converter != null) {
                return new RowMapper() {
                    public Object map(ResultSet rs) throws SQLException {
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
