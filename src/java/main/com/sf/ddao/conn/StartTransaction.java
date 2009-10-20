package com.sf.ddao.conn;

import com.sf.ddao.chain.ChainInvocationContext;
import com.sf.ddao.chain.ChainMemberInvocationHandler;

import java.sql.Connection;

/**
 * Date: Oct 19, 2009
 * Time: 4:24:24 PM
 */
public class StartTransaction implements ChainMemberInvocationHandler {
    public Object invoke(ChainInvocationContext context, boolean hasNext) throws Throwable {
        final Connection connection = ConnectionHandlerHelper.getConnection(context);
        ConnectionHandlerHelper.putConnectionOnHold(connection);
        return connection;
    }
}
