package com.sf.ddao.astore;

import com.sf.ddao.JDBCDao;
import com.sf.ddao.TestUserBean;

import java.util.List;
import java.util.Map;

/**
 * Created by psyrtsov
 */
@JDBCDao(value = "jdbc://test", driver = "com.mockrunner.mock.jdbc.MockDriver")
@AsyncStore(TestAsyncStore.class)
public interface UserDao {
    @AsyncDBGet(sql = "select id, name from user where id = #id#", cacheKey = "id")
    TestUserBean getUser(TestUserBean userBean);

    @AsyncDBBatchGet(sql = "select id, name from user where id in ($ctx:keyList$)")
    Map<Integer, TestUserBean> getUserList(List<Integer> idList);

    @AsyncDBPut(sql = "update user set name = #name# where id = #id#", cacheKey = "id")
    void updateUser(TestUserBean user);
}
