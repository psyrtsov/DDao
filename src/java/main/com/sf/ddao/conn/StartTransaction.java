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

package com.sf.ddao.conn;

import com.sf.ddao.chain.CtxHelper;
import com.sf.ddao.chain.MethodCallCtx;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import java.sql.Connection;

/**
 * Date: Oct 19, 2009
 * Time: 4:24:24 PM
 */
public class StartTransaction implements Command {
    public boolean execute(Context context) throws Exception {
        final MethodCallCtx callCtx = CtxHelper.get(context, MethodCallCtx.class);
        final Connection connection = ConnectionHandlerHelper.getConnection(context);
        ConnectionHandlerHelper.putConnectionOnHold(context);
        callCtx.setLastReturn(connection);
        return CONTINUE_PROCESSING;
    }
}
