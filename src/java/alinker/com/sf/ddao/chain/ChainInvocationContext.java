package com.sf.ddao.chain;

/**
 * Created by pavel
 * Date: Aug 6, 2009
 * Time: 3:23:43 PM
 */
public class ChainInvocationContext {
    private Object[] args;
    private Object lastReturn;

    public void setArgs(Object[] args) {
        this.args = args;
    }

    public Object[] getArgs() {
        return args;
    }

    public void setLastReturn(Object returnValue) {
        this.lastReturn = returnValue;
    }

    public Object getLastReturn() {
        return lastReturn;
    }
}
