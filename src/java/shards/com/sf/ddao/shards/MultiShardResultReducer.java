package com.sf.ddao.shards;

import java.util.List;

/**
 * Created by psyrtsov
 */
public interface MultiShardResultReducer<T> {
    T reduce(List<T> resultList);
}
