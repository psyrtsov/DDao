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

package com.sf.ddao.astore;

import com.sf.ddao.TestUserBean;
import org.apache.commons.chain.Command;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * Created by psyrtsov
 */
public class TestAsyncStore implements AsyncDB<Long, TestUserBean> {
    public void put(Long key, TestUserBean value, Command dbPut) {
        //psdo: verify generated code for com.sf.ddao.astore.TestAsyncStore put
        System.err.println("");
    }

    public TestUserBean get(Long key, Callable<TestUserBean> dbGet) {
        //psdo: verify generated code for com.sf.ddao.astore.TestAsyncStore get
        return new TestUserBean(true);
    }

    public Map<Long, TestUserBean> batchGet(Collection<Long> keys, DBBatchGet<Long, TestUserBean> dbBatchGet) {
        return dbBatchGet.batchGet(keys);
    }
}
