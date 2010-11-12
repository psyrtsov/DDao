package com.sf.ddao.astore;

import com.sf.ddao.astore.impl.AsyncDBBatchGetOperation;
import org.apache.commons.chain.Context;

import java.util.Collection;
import java.util.Map;

/**
 * Created by psyrtsov
 */
public interface DBBatchGet<K, T> {
    void init(AsyncDBBatchGetOperation operation, Context context);

    Map<K, T> batchGet(Collection<K> keys);
}
