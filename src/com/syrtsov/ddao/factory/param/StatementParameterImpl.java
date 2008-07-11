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

package com.syrtsov.ddao.factory.param;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.math.NumberUtils;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * StatementParameter object extracts single value from method call argument list
 * and binds that value to given prepared statement
 * <p/>
 * Created by Pavel Syrtsov
 * Date: Nov 5, 2007
 * Time: 8:48:16 PM
 */
// psdo: implement support for static method calls from template
public class StatementParameterImpl extends StatementParameterHelper {
    private Integer num = null;

    public void init(Method method, String name) {
        if (NumberUtils.isNumber(name)) {
            num = NumberUtils.createInteger(name);
        }
        super.init(method, name);
    }

    public Object extractData(Object[] args) throws StatementParameterException {
        if (num != null) {
            if (args.length <= num) {
                throw new StatementParameterException("Query refers to argument #" + num + ", while method has only " + args.length + " arguments");
            }
            return args[num];
        }
        Object param = args[0];
        if (param instanceof Map) {
            return ((Map) param).get(name);
        }
        try {
            return PropertyUtils.getProperty(param, name);
        } catch (Exception e) {
            throw new StatementParameterException("Failed to get statement paramter " + name + " from " + param);
        }
    }

}
