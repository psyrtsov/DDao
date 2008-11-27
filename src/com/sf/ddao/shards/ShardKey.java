package com.sf.ddao.shards;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * todo: add class comments
 * Created-By: Pavel Syrtsov
 * Date: Jun 22, 2008
 * Time: 9:27:08 AM
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface ShardKey {
    String value() default "";
}
