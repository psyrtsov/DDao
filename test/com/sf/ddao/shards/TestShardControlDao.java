package com.sf.ddao.shards;

import com.sf.ddao.alinker.factory.Singleton;
import org.apache.commons.chain.Context;

import javax.sql.DataSource;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by psyrtsov
 */
@Singleton
public class TestShardControlDao implements ShardControlDao<Integer> {
    private DataSource ds1;
    private DataSource ds2;

    public DataSource getShard(Integer id, Context ctx) {
        if (1 <= id && id <= 10) {
            return ds1;
        }
        if (11 <= id && id <= 20) {
            return ds2;
        }
        return null;
    }

    public Map<DataSource, Collection<Integer>> getMultiShard(Collection<Integer> shardKeyCollection, Context context) {
        Map<DataSource, Collection<Integer>> res = new HashMap<DataSource, Collection<Integer>>();
        res.put(ds1, shardKeyCollection);
        res.put(ds2, shardKeyCollection);
        return res;
    }

    public void setDS1(DataSource DS1) {
        this.ds1 = DS1;
    }

    public void setDS2(DataSource DS2) {
        this.ds2 = DS2;
    }
}
