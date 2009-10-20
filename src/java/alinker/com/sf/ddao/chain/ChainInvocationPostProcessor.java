package com.sf.ddao.chain;

/**
 * Created by pavel
 * Date: Sep 19, 2009
 * Time: 11:40:24 AM
 */
public interface ChainInvocationPostProcessor {
    void chainPostProcess(ChainInvocationContext context) throws Throwable;
}
