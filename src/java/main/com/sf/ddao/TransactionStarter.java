package com.sf.ddao;

import com.sf.ddao.chain.ChainMember;
import com.sf.ddao.ops.SelectSqlOperation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Date: Oct 19, 2009
 * Time: 4:23:34 PM
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@ChainMember(SelectSqlOperation.class)
public @interface TransactionStarter {
}
