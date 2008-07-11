package com.syrtsov.shards;

import com.syrtsov.alinker.factory.UseFactory;
import com.syrtsov.handler.HandlerAnnotation;
import com.syrtsov.handler.HandlerFactory;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * todo: add class comments
 * Created-By: Pavel Syrtsov
 * Date: Jun 19, 2008
 * Time: 11:25:07 AM
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@HandlerAnnotation(ShardedJNDIDataSourceHandler.class)
@UseFactory(HandlerFactory.class)
public @interface ShardedJNDIDao {
    /**
     * @return  key to shards table
     */
    String value();
}
