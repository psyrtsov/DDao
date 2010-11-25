package com.sf.ddao.shards;

import com.sf.ddao.alinker.factory.UseFactory;
import com.sf.ddao.chain.ChainHandlerFactory;
import com.sf.ddao.chain.CommandAnnotation;
import com.sf.ddao.shards.conn.ShardedConnectionHandler;

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
@UseFactory(ChainHandlerFactory.class)
@CommandAnnotation(ShardedConnectionHandler.class)
public @interface ShardedDao {
    Class<? extends ShardControlDao> value();
}
