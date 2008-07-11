package com.syrtsov.shards;

import com.syrtsov.ddao.JNDIDao;
import com.syrtsov.ddao.Select;

import java.util.List;

/**
 * todo: add class comments
 * Created-By: Pavel Syrtsov
 * Date: Jun 19, 2008
 * Time: 12:06:34 PM
 */
@JNDIDao("jdbc/shardControlDB")
public interface ShardControlDao {
    @Select("select startId, endId, dsName from shard where setName = $0$")
    List<Shard> getShardList(String shardSetKey);
}
