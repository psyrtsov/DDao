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
