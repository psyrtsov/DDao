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

package com.syrtsov.ddao.ir;

import com.syrtsov.ddao.alinker.ALinker;
import com.syrtsov.ddao.alinker.CachingFactory;
import com.syrtsov.ddao.alinker.Context;
import com.syrtsov.ddao.alinker.FactoryException;

/**
 * Created-By: Pavel Syrtsov
 * Date: Apr 10, 2008
 * Time: 6:00:17 PM
 */
public class IRFactory implements CachingFactory<Object> {
    public Object create(ALinker aLinker, Context<Object> ctx) throws FactoryException {
        String spaceName = getSpaceName(ctx);
        return null;  // PSDO: review generated methd body
    }

    private String getSpaceName(Context<Object> ctx) {
        return null;  // PSDO: review generated methd body
    }

    public Object getCachedObject(Context<Object> ctx) {
        return null;  // PSDO: review generated methd body
    }
}
