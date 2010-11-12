package com.sf.ddao.astore;

import org.apache.commons.chain.Command;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * Created by psyrtsov
 */
public interface AsyncDB<K, V> {
    void put(K key, V value, Command dbPut);

    V get(K key, Callable<V> dbGet);

    Map<K, V> batchGet(Collection<K> keys, DBBatchGet<K, V> dbBatchGet);
}
