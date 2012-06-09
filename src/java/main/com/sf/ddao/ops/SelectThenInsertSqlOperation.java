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

package com.sf.ddao.ops;

import com.sf.ddao.SelectThenInsert;
import com.sf.ddao.chain.CtxHelper;
import com.sf.ddao.chain.InitializerException;
import com.sf.ddao.chain.MethodCallCtx;
import com.sf.ddao.factory.param.ThreadLocalParameter;
import org.apache.commons.chain.Context;

import javax.inject.Inject;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.Arrays;

/**
 * User: Pavel Syrtsov
 * Date: Aug 2, 2008
 * Time: 9:12:57 PM
 */
public class SelectThenInsertSqlOperation extends UpdateSqlOperation {
    @Inject
    private SelectSqlOperation selectSqlOp;
    public static final String ID_FIELD_NAME = "id";

    @Override
    public boolean execute(Context context) throws Exception {
        try {
            final MethodCallCtx callCtx = CtxHelper.get(context, MethodCallCtx.class);
            selectSqlOp.execute(context);
            Object res = callCtx.getLastReturn();
            ThreadLocalParameter.put(ID_FIELD_NAME, res);
            super.execute(context);
            callCtx.setLastReturn(res);
            return CONTINUE_PROCESSING;
        } finally {
            ThreadLocalParameter.remove(ID_FIELD_NAME);
        }
    }

    @Override
    public void init(AnnotatedElement element, Annotation annotation) {
        SelectThenInsert selectThenInsert = element.getAnnotation(SelectThenInsert.class);
        String sql[] = selectThenInsert.value();
        if (sql.length != 2) {
            throw new InitializerException(SelectThenInsert.class.getSimpleName() + " annotation has to have 2 sql statments, but got:"
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
