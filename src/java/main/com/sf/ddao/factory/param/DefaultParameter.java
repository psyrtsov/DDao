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

package com.sf.ddao.factory.param;

import com.sf.ddao.chain.CtxHelper;
import com.sf.ddao.chain.MethodCallCtx;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.chain.Context;
import org.apache.commons.lang.math.NumberUtils;

import java.lang.reflect.AnnotatedElement;
import java.util.Map;

/**
 * StatementParameter object extracts single value from method call argument list
 * and binds that value to given prepared statement
 * <p/>
 * Created by Pavel Syrtsov
 * Date: Nov 5, 2007
 * Time: 8:48:16 PM
 */
public class DefaultParameter extends ParameterHelper {
    public static final int RETURN_ARG_IDX = -1;
    private Integer num = null;
    private String propName = null;

    @Override
    public void init(AnnotatedElement element, String name, boolean isRef) {
        int dotIndex = name.indexOf(".");
        String numStr = name;
        if (dotIndex > 0) {
            numStr = name.substring(0, dotIndex);
            propName = name.substring(dotIndex + 1);
        }
        if (NumberUtils.isNumber(numStr)) {
            num = NumberUtils.createInteger(numStr);
        } else {
            propName = name;
        }
        super.init(element, name, isRef);
    }

    public Object extractParam(Context context) throws ParameterException {
        final MethodCallCtx callCtx = CtxHelper.get(context, MethodCallCtx.class);
        Object[] args = callCtx.getArgs();
        Object param;
        if (num != null) {
            if (num == RETURN_ARG_IDX) {
                param = callCtx.getLastReturn();
            } else {
                if (args.length <= num) {
                    throw new ParameterException("Query refers to argument #" + num + ", while method has only " + args.length + " arguments");
                }
                param = args[num];
            }
        } else {
            param = args[0];
        }
        if (propName == null) {
            return param;
        }
        if (param instanceof Map) {
            return ((Map) param).get(propName);
        }
        try {
            return PropertyUtils.getProperty(param, propName);
        } catch (Exception e) {
            throw new ParameterException("Failed to get statement parameter " + name + " from " + param, e);
        }
    }

}
