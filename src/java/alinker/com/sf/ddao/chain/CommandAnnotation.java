package com.sf.ddao.chain;

import org.apache.commons.chain.Command;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.ANNOTATION_TYPE})
public @interface CommandAnnotation {
    Class<? extends Command> value();
}
