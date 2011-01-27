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

package com.sf.ddao.crud.param;

import com.sf.ddao.chain.CtxHelper;
import com.sf.ddao.chain.MethodCallCtx;
import com.sf.ddao.crud.CRUDDao;
import com.sf.ddao.factory.param.ParameterException;
import com.sf.ddao.factory.param.ParameterHandler;
import com.sf.ddao.factory.param.ParameterHelper;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.chain.Context;

import java.beans.PropertyDescriptor;
import java.lang.reflect.AnnotatedElement;
import java.sql.PreparedStatement;
import java.text.MessageFormat;

/**
 * Date: Oct 27, 2009
 * Time: 3:36:12 PM
 */
public class CRUDBeanPropsParameter implements ParameterHandler {
    public static final String CRUD_BEAN_PROPS = "crudBeanProps";
    private PropertyDescriptor[] descriptors;
    private int argNum;
    private String fmt;

    public void init(AnnotatedElement element, String param, boolean isRef) {
        int commaIdx = param.indexOf(",");
        if (commaIdx < 0) {
            if (isRef) {
                fmt = "?";
            } else {
                fmt = "{0}";
            }
        } else {
            fmt = param.substring(commaIdx + 1);
            param = param.substring(0, commaIdx);
        }
        argNum = Integer.parseInt(param);
    }

    public String extractParam(Context context) throws ParameterException {
        throw new UnsupportedOperationException();
    }

    public void appendParam(Context ctx, StringBuilder sb) throws ParameterException {
        if (descriptors == null) {
            init(ctx);
        }
        int c = 0;
        for (PropertyDescriptor descriptor : descriptors) {
            if (CRUDDao.IGNORED_PROPS.contains(descriptor.getName())) {
                continue;
            }
            if (c > 0) {
                sb.append(",");
            }
            final String fieldName = mapPropName2Field(descriptor);
            sb.append(MessageFormat.format(fmt, fieldName));
            c++;
        }
    }

    public static String mapPropName2Field(PropertyDescriptor descriptor) {
        final String s = descriptor.getName();
        StringBuilder sb = new StringBuilder(s.length());
        for (char ch : s.toCharArray()) {
            if (Character.isUpperCase(ch)) {
                ch = Character.toLowerCase(ch);
                sb.append("_").append(ch);
            } else {
                sb.append(ch);
            }
        }
        return sb.toString();
    }

    public int bindParam(PreparedStatement preparedStatement, int idx, Context ctx) throws ParameterException {
        if (descriptors == null) {
            init(ctx);
        }
        int c = 0;
        try {
            final MethodCallCtx callCtx = CtxHelper.get(ctx, MethodCallCtx.class);
            Object[] args = callCtx.getArgs();
            final Object bean = args[argNum];
            for (PropertyDescriptor descriptor : descriptors) {
                if (CRUDDao.IGNORED_PROPS.contains(descriptor.getName())) {
                    continue;
                }
                Object v = descriptor.getReadMethod().invoke(bean);
                ParameterHelper.bind(preparedStatement, idx++, v);
                c++;
            }
        } catch (Exception e) {
            throw new ParameterException(e);
        }
        return c;
    }

    private synchronized void init(Context ctx) {
        if (descriptors != null) {
            return;
        }
        Class<?> beanClass = CRUDParameterService.getCRUDDaoBean(ctx);
        descriptors = PropertyUtils.getPropertyDescriptors(beanClass);
    }
}
