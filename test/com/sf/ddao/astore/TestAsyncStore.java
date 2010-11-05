package com.sf.ddao.astore;

import com.sf.ddao.TestUserBean;
import org.apache.commons.chain.Command;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * Created by psyrtsov
 */
public class TestAsyncStore implements AsyncDB<Integer, TestUserBean> {
    public void put(Integer key, TestUserBean value, Command dbPut) {
        //psdo: verify generated code for com.sf.ddao.astore.TestAsyncStore put
        System.err.println("");
    }

    public TestUserBean get(Integer key, Callable<TestUserBean> dbGet) {
        //psdo: verify generated code for com.sf.ddao.astore.TestAsyncStore get
        return new TestUserBean();
    }

    public Map<Integer, TestUserBean> batchGet(Collection<Integer> keys, DBBatchGet<Integer, TestUserBean> dbBatchGet) {
        return dbBatchGet.batchGet(keys);
    }
}
