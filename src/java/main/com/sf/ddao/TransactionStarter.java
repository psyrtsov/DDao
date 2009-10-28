package com.sf.ddao;

import com.sf.ddao.chain.CommandAnnotation;
import com.sf.ddao.conn.StartTransaction;

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
@CommandAnnotation(StartTransaction.class)
public @interface TransactionStarter {
}
