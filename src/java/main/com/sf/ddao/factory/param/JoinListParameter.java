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

import org.apache.commons.chain.Context;

import java.util.Collection;
import java.util.List;

/**
 * Date: Oct 27, 2009
 * Time: 3:36:12 PM
 */
public class JoinListParameter extends DefaultParameter {
    public Object extractParam(Context context) throws ParameterException {
        final List list = (List) super.extractParam(context);
        return join(list);
    }

    public static String join(Collection list) {
        StringBuilder sb = new StringBuilder();
        for (Object item : list) {
            if (sb.length() > 0) {
                sb.append(',');
            }
            sb.append(item);
        }
        return sb.toString();
    }
}
