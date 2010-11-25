package com.sf.ddao.shards;

import org.apache.commons.chain.Context;

import javax.sql.DataSource;
import java.util.Collection;
import java.util.Map;

/**
 * todo: add class comments
 * Created-By: Pavel Syrtsov
 * Date: Jun 19, 2008
 * Time: 12:06:34 PM
 */
public interface ShardControlDao<K> {
    DataSource getShard(K shardKey, Context ctx);

    Map<DataSource, Collection<K>> getMultiShard(Collection<K> shardKeyCollection, Context context);
}
