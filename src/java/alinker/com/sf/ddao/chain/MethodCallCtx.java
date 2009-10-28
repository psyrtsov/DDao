package com.sf.ddao.chain;

import java.lang.reflect.Method;

/**
 * Date: Oct 23, 2009
 * Time: 4:14:06 PM
 */
@SuppressWarnings({"unchecked"})
public class MethodCallCtx {
    private Method method;
    private Object[] args;
    private Object lastReturn;

    public MethodCallCtx(Object[] args, Method method) {
        this.args = args;
        this.method = method;
    }

    public Object[] getArgs() {
        return args;
    }

    public void setArgs(Object[] args) {
        this.args = args;
    }

    public Object getLastReturn() {
        return lastReturn;
    }

    public void setLastReturn(Object lastReturn) {
        this.lastReturn = lastReturn;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }
}
