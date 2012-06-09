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

import com.sf.ddao.DaoException;
import com.sf.ddao.chain.CtxHelper;
import com.sf.ddao.chain.InitializerException;
import com.sf.ddao.chain.MethodCallCtx;
import com.sf.ddao.crud.SelectWithCallbackThenUpdate;
import com.sf.ddao.crud.UpdateCallback;
import com.sf.ddao.ops.SelectSqlOperation;
import com.sf.ddao.ops.UpdateSqlOperation;
import org.apache.commons.chain.Context;

import javax.inject.Inject;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.Arrays;

/**
 * User: Tomi Joki-Korpela
 * Date: Feb 4, 2011
 * Time: 9:12:57 PM
 */
public class SelectWithCallbackThenUpdateSqlOperation extends UpdateSqlOperation {
    @Inject
    private SelectSqlOperation selectSqlOp;

    @Override
    public boolean execute(Context context) throws Exception {
        final MethodCallCtx callCtx = CtxHelper.get(context, MethodCallCtx.class);
        try {
            selectSqlOp.execute(context);
            Object res = callCtx.getLastReturn();

            Object[] args = callCtx.getArgs();
            for (Object arg : args) {
                if (UpdateCallback.class.isAssignableFrom(arg.getClass())) {
                    UpdateCallback callback = UpdateCallback.class.cast(arg);
                    //noinspection unchecked
                    callback.update(res);
                }
            }

            super.execute(context);
            return CONTINUE_PROCESSING;
        } catch (Exception t) {
            throw new DaoException("Failed to execute sql operation for " + callCtx.getMethod(), t);
        }
    }

    @Override
    public void init(AnnotatedElement element, Annotation annotation) {
        SelectWithCallbackThenUpdate selectWithCallbackThenUpdate = element.getAnnotation(SelectWithCallbackThenUpdate.class);
        String sql[] = selectWithCallbackThenUpdate.value();
        if (sql.length != 2) {
            throw new InitializerException(SelectWithCallbackThenUpdate.class.getSimpleName() + " annotation has to have 2 sql statments, but got:"
                    + Arrays.toString(sql) + ", for method " + element);
        }
        try {
            selectSqlOp.init(element, sql[0]);
            super.init(element, sql[1]);
        } catch (Exception e) {
            throw new InitializerException("Failed to setup sql operations " + Arrays.toString(sql) + " for method " + element, e);
        }
    }
}
