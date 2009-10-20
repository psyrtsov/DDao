package com.sf.ddao.kvs.impl;

import com.sf.ddao.alinker.ALinker;
import com.sf.ddao.alinker.Factory;
import com.sf.ddao.alinker.factory.DefaultFactoryManager;
import com.sf.ddao.alinker.factory.FactoryService;
import com.sf.ddao.kvs.KeyValueStore;

/**
 * Created by pavel
 * Date: Jul 23, 2009
 * Time: 10:36:40 PM
 */
public abstract class KVSFactoryBase implements Factory<KeyValueStore>, FactoryService {
    public void register(ALinker aLinker, DefaultFactoryManager defaultFactoryManager) {
        aLinker.init(this);
        defaultFactoryManager.register(KeyValueStore.class, this);
    }
}
