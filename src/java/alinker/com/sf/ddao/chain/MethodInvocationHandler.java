package com.sf.ddao.chain;

import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;

/**
 * Created by pavel
 * Date: Aug 6, 2009
 * Time: 2:29:05 PM
 */
class MethodInvocationHandler {
    private final Method method;
    private final List<ChainMemberInvocationHandler> chainMemberInvocationHandlers;
    private int chainContextParamIndex = -1;

    public MethodInvocationHandler(Method method, List<ChainMemberInvocationHandler> chainMemberInvocationHandlers) {
        this.method = method;
        this.chainMemberInvocationHandlers = chainMemberInvocationHandlers;
        final Class<?>[] parameterTypes = method.getParameterTypes();
        for (int i = 0; i < parameterTypes.length; i++) {
            if (ChainInvocationContext.class.isAssignableFrom(parameterTypes[i])) {
                chainContextParamIndex = i;
                break;
            }
        }
    }

    public Object invoke(Object[] args) throws Throwable {
        ChainInvocationContext chainInvocationContext = createContext(args);
        for (Iterator<ChainMemberInvocationHandler> it = chainMemberInvocationHandlers.iterator(); it.hasNext();) {
            ChainMemberInvocationHandler chainMemberInvocationHandler = it.next();
            Object res = chainMemberInvocationHandler.invoke(chainInvocationContext, it.hasNext());
            chainInvocationContext.setReturn(res);
        }
        if (ChainInvocationContext.class.isAssignableFrom(method.getReturnType())) {
            return chainInvocationContext;
        }
        return chainInvocationContext.getAReturn();
    }

    private ChainInvocationContext createContext(Object[] args) {
        ChainInvocationContext res;
        if (chainContextParamIndex >= 0) {
            res = (ChainInvocationContext) args[chainContextParamIndex];
            if (res == null) {
                throw new NullPointerException("ChainInvocationContext parameter is null!");
            }
        } else {
            res = new ChainInvocationContext();
        }
        res.setArgs(args);
        return res;
    }
}
