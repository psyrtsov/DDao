package com.sf.ddao.chain;

/**
 * Created by pavel
 * Date: Aug 6, 2009
 * Time: 2:32:02 PM
 */
public interface ChainMemberInvocationHandler {
    /**
     * @param chainInvocationContext - invocation context
     * @param hasNext                - if false then this is last invocation on the chain
     *                               ans result will be used as return value for whole the method call
     * @return value will be stored in lastReturn property of context
     */
    Object invoke(ChainInvocationContext chainInvocationContext, boolean hasNext);
}
