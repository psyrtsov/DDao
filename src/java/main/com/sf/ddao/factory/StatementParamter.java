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

package com.sf.ddao.factory;

import org.apache.commons.chain.Context;

import java.sql.SQLException;

/**
 *
 */
public interface StatementParamter extends BoundParameter {
    /**
     * this method should append string presentation of
     * parameter that will be inlined in query text or binding mark if this is ref parameter,
     *
     * @return value extracted from argument list
     * @throws com.sf.ddao.factory.param.ParameterException
     *          - thrown when failed to extract parameter
     */
    void appendParam(Context context, StringBuilder sb) throws SQLException;

}
