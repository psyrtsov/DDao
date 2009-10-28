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
