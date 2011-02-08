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

import com.sf.ddao.factory.StatementParamter;
import org.apache.commons.chain.Context;

import java.lang.reflect.AnnotatedElement;
import java.sql.SQLException;

/**
 * StatementParameter defines ability of object to extract value from method argument list
 * that can be passed as parameter to prepared statement.
 * <p/>
 * Created by Pavel Syrtsov
 * Date: Nov 27, 2007
 * Time: 7:07:45 PM
 */
public interface ParameterHandler extends StatementParamter {
    /**
     * @param element - method object
     * @param isRef
     */
    void init(AnnotatedElement element, String param, boolean isRef);

    /**
     * used for direct access to data object by some internal classes
     * psdo: we might want to remove it from iface
     *
     * @param context
     * @return
     * @throws ParameterException
     */
    Object extractParam(Context context) throws SQLException;
}
