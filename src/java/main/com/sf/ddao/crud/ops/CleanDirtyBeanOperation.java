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

package com.sf.ddao.crud.ops;

import com.sf.ddao.chain.CtxHelper;
import com.sf.ddao.chain.MethodCallCtx;
import com.sf.ddao.crud.DirtyableBean;
import org.apache.commons.chain.Context;
import org.apache.commons.chain.Filter;

/**
 * Created by psyrtsov
 */
public class CleanDirtyBeanOperation implements Filter {

    public boolean execute(Context context) throws Exception {
        return CONTINUE_PROCESSING;
    }

    public boolean postprocess(Context context, Exception e) {
        final MethodCallCtx callCtx = CtxHelper.get(context, MethodCallCtx.class);
        final Object[] args = callCtx.getArgs();
        for (Object arg : args) {
            if (arg instanceof DirtyableBean) {
                DirtyableBean dirtyableBean = (DirtyableBean) arg;
                dirtyableBean.cleanDirtyBean();
            }
        }
        return CONTINUE_PROCESSING;
    }
}
