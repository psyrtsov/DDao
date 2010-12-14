/*
 * Copyright 2008 Pavel Syrtsov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and limitations
 *  under the License.
 */

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
public class TestShardingService implements ShardingService<Integer> {
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
