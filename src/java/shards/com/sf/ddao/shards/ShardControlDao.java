package com.sf.ddao.shards;

import org.apache.commons.chain.Context;

import javax.sql.DataSource;

/**
 * todo: add class comments
 * Created-By: Pavel Syrtsov
 * Date: Jun 19, 2008
 * Time: 12:06:34 PM
 */
public interface ShardControlDao<K> {
    DataSource getShard(K shardKey, Context ctx);
}
