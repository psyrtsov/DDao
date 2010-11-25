package com.sf.ddao.shards;

import com.sf.ddao.alinker.factory.Singleton;
import org.apache.commons.chain.Context;

import javax.sql.DataSource;

/**
 * Created by psyrtsov
 */
@Singleton
public class TestShardControlDao implements ShardControlDao {
    private DataSource ds1;
    private DataSource ds2;

    public DataSource getShard(Object shardKey, Context ctx) {
        Integer id = (Integer) shardKey;
        if (1 <= id && id <= 10) {
            return ds1;
        }
        if (11 <= id && id <= 20) {
            return ds2;
        }
        return null;
    }

    public void setDS1(DataSource DS1) {
        this.ds1 = DS1;
    }

    public void setDS2(DataSource DS2) {
        this.ds2 = DS2;
    }
}
