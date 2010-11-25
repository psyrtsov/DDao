package com.sf.ddao.shards.ops;

import com.sf.ddao.shards.MultiShardResultMerger;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by psyrtsov
 */
public class ListMultiShardResultMerger implements MultiShardResultMerger<List<Object>> {
    public List<Object> reduce(List<List<Object>> resultList) {
        List<Object> res = new ArrayList<Object>();
        for (List<Object> list : resultList) {
            res.addAll(list);
        }
        return res;
    }
}
