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
import com.sf.ddao.orm.rsmapper.rowmapper.*;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.Converter;

import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
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
            RowMapperFactory rowMapperFactory = getRowMapperFactory(itemType);
            return new ArrayRSMapper(rowMapperFactory, itemType);
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
            RowMapperFactory keyMapperFactory = getScalarMapper(keyType, 1, true);
            RowMapperFactory valueMapperFactory = getRowMapperFactory(valueType, 2);
            return new MapRSMapper(keyMapperFactory, valueMapperFactory);
        }
        return new SingleRowRSMapper(getRowMapperFactory(returnType));
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
        RowMapperFactory rowMapperFactory = getRowMapperFactory(itemType);
        //noinspection unchecked
        return new CollectionRSMapper(rowMapperFactory);
    }

    public static RowMapperFactory getRowMapperFactory(Type itemType) {
        return getRowMapperFactory(itemType, 1);
    }

    public static RowMapperFactory getRowMapperFactory(Type itemType, int startIdx) {
        // see if return type is simple so that we should map just startIdx column 
        RowMapperFactory scalarMapperFactory = getScalarMapper(itemType, startIdx, false);
        if (scalarMapperFactory != null) {
            return scalarMapperFactory;
        }
        if (itemType instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) itemType;
            final Type rawType = parameterizedType.getRawType();
            if (rawType instanceof Class && Map.class.isAssignableFrom((Class<?>) rawType)) {
                return new RowMapperFactory() {
                    public RowMapper get() {
                        return new MapRowMapper();
                    }
                };
            }
        }
        if (itemType instanceof Class) {
            final Class itemClass = (Class) itemType;
            if (RowMapper.class.isAssignableFrom(itemClass)) {
                //noinspection unchecked
                return new SelfRowMapperFactory(itemClass);
            }
            if (Map.class.isAssignableFrom(itemClass)) {
                return new RowMapperFactory() {
                    public RowMapper get() {
                        return new MapRowMapper();
                    }
                };
            }
            return new BeanRowMapperFactory(itemClass);
        }
        throw new DaoException("No mapping defined for type " + itemType);
    }

    public static RowMapperFactory getScalarMapper(final Type itemType, final int idx, boolean req) {
        if (itemType == String.class) {
            return new ScalarRMF() {
                public Object map(ResultSet rs) throws SQLException {
                    return rs.getString(idx);
                }
            };
        }
        if (itemType == Integer.class || itemType == Integer.TYPE) {
            return new ScalarRMF() {
                public Object map(ResultSet rs) throws SQLException {
                    return rs.getInt(idx);
                }
            };
        }
        if (itemType == URL.class) {
            return new ScalarRMF() {
                public Object map(ResultSet rs) throws SQLException {
                    return rs.getURL(idx);
                }
            };
        }
        if (itemType == BigInteger.class) {
            return new ScalarRMF() {
                public Object map(ResultSet rs) throws SQLException {
                    final BigDecimal res = rs.getBigDecimal(idx);
                    return res == null ? null : res.toBigInteger();
                }
            };
        }
        if (itemType == BigDecimal.class) {
            return new ScalarRMF() {
                public Object map(ResultSet rs) throws SQLException {
                    return rs.getBigDecimal(idx);
                }
            };
        }
        if (itemType == InputStream.class) {
            return new ScalarRMF() {
                public Object map(ResultSet rs) throws SQLException {
                    return rs.getBinaryStream(idx);
                }
            };
        }
        if (itemType == Boolean.class || itemType == Boolean.TYPE) {
            return new ScalarRMF() {
                public Object map(ResultSet rs) throws SQLException {
                    return rs.getBoolean(idx);
                }
            };
        }
        if (itemType == Blob.class) {
            return new ScalarRMF() {
                public Object map(ResultSet rs) throws SQLException {
                    return rs.getBlob(idx);
                }
            };
        }
        if (itemType == java.sql.Date.class || itemType == java.util.Date.class) {
            return new ScalarRMF() {
                public Object map(ResultSet rs) throws SQLException {
                    return rs.getTimestamp(idx);
                }
            };
        }
        if (itemType instanceof Class) {
            final Class itemClass = (Class) itemType;
            final ColumnMapper columnMapper = ColumnMapperRegistry.lookup(itemClass);
            if (columnMapper != null) {
                return new ScalarRMF() {
                    public Object map(ResultSet rs) throws SQLException {
                        return columnMapper.map(rs, idx);
                    }
                };
            }
            final Converter converter = ConvertUtils.lookup(itemClass);
            if (converter != null) {
                return new ScalarRMF() {
                    public Object map(ResultSet rs) throws SQLException {
                        String s = rs.getString(idx);
                        if (s == null) {
                            return null;
                        }
                        return converter.convert(itemClass, s);
                    }
                };
            }
            if (Enum.class.isAssignableFrom((Class<?>) itemType)) {
                return new ScalarRMF() {
                    public Object map(ResultSet rs) throws SQLException {
                        String s = rs.getString(idx);
                        if (s == null) {
                            return null;
                        }
                        //noinspection unchecked
                        return Enum.valueOf((Class<Enum>) itemType, s);
                    }
                };
            }
        }
        if (req) {
            throw new IllegalArgumentException("no mapping defined for " + itemType);
        }
        return null;
    }

    //psdo: merge this with index based scalar mapper

    public static RowMapperFactory getScalarRowMapperFactory(final Type itemType, final String name, boolean req) {
        if (itemType == String.class) {
            return new ScalarRMF() {
                public Object map(ResultSet rs) throws SQLException {
                    return rs.getString(name);
                }
            };
        }
        if (itemType == Integer.class || itemType == Integer.TYPE) {
            return new ScalarRMF() {
                public Object map(ResultSet rs) throws SQLException {
                    return rs.getInt(name);
                }
            };
        }
        if (itemType == URL.class) {
            return new ScalarRMF() {
                public Object map(ResultSet rs) throws SQLException {
                    return rs.getURL(name);
                }
            };
        }
        if (itemType == BigInteger.class) {
            return new ScalarRMF() {
                public Object map(ResultSet rs) throws SQLException {
                    final BigDecimal res = rs.getBigDecimal(name);
                    return res == null ? null : res.toBigInteger();
                }
            };
        }
        if (itemType == BigDecimal.class) {
            return new ScalarRMF() {
                public Object map(ResultSet rs) throws SQLException {
                    return rs.getBigDecimal(name);
                }
            };
        }
        if (itemType == InputStream.class) {
            return new ScalarRMF() {
                public Object map(ResultSet rs) throws SQLException {
                    return rs.getBinaryStream(name);
                }
            };
        }
        if (itemType == Boolean.class || itemType == Boolean.TYPE) {
            return new ScalarRMF() {
                public Object map(ResultSet rs) throws SQLException {
                    return rs.getBoolean(name);
                }
            };
        }
        if (itemType == Blob.class) {
            return new ScalarRMF() {
                public Object map(ResultSet rs) throws SQLException {
                    return rs.getBlob(name);
                }
            };
        }
        if (itemType == java.sql.Date.class || itemType == java.util.Date.class) {
            return new ScalarRMF() {
                public Object map(ResultSet rs) throws SQLException {
                    return rs.getTimestamp(name);
                }
            };
        }
        if (itemType instanceof Class) {
            final Class itemClass = (Class) itemType;
            final ColumnMapper columnMapper = ColumnMapperRegistry.lookup(itemClass);
            if (columnMapper != null) {
                return new ScalarRMF() {
                    public Object map(ResultSet rs) throws SQLException {
                        return columnMapper.map(rs, name);
                    }
                };
            }
            final Converter converter = ConvertUtils.lookup(itemClass);
            if (converter != null) {
                return new ScalarRMF() {
                    public Object map(ResultSet rs) throws SQLException {
                        String s = rs.getString(name);
                        if (s == null) {
                            return null;
                        }
                        return converter.convert(itemClass, s);
                    }
                };
            }
            if (Enum.class.isAssignableFrom((Class<?>) itemType)) {
                return new ScalarRMF() {
                    public Object map(ResultSet rs) throws SQLException {
                        String s = rs.getString(name);
                        if (s == null) {
                            return null;
                        }
                        //noinspection unchecked
                        return Enum.valueOf((Class<Enum>) itemType, s);
                    }
                };
            }
        }
        if (req) {
            throw new IllegalArgumentException("no mapping defined for " + itemType);
        }
        return null;
    }
}
