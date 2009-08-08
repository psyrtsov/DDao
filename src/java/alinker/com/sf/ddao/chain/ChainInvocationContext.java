package com.sf.ddao.chain;

/**
 * Created by pavel
 * Date: Aug 6, 2009
 * Time: 3:23:43 PM
 */
public class ChainInvocationContext {
    private Object[] args;
    private Object aReturn;

    public void setArgs(Object[] args) {
        this.args = args;
    }

    public Object[] getArgs() {
        return args;
    }

    public void setReturn(Object aReturn) {
        this.aReturn = aReturn;
    }

    public Object getAReturn() {
        return aReturn;
    }
}
