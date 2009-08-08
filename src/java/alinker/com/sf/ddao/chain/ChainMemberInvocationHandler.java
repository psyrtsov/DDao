package com.sf.ddao.chain;

/**
 * Created by pavel
 * Date: Aug 6, 2009
 * Time: 2:32:02 PM
 */
public interface ChainMemberInvocationHandler {
    Object invoke(ChainInvocationContext chainInvocationContext, boolean hasNext);
}
