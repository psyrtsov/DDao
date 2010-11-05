package com.sf.ddao.astore;

import com.mockrunner.jdbc.BasicJDBCTestCaseAdapter;
import com.sf.ddao.TestUserBean;
import com.sf.ddao.alinker.ALinker;
import org.mockejb.jndi.MockContextFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by psyrtsov
 */
public class AsyncStoreTest extends BasicJDBCTestCaseAdapter {
    ALinker factory;

    protected void setUp() throws Exception {
        super.setUp();
        this.factory = new ALinker();
    }


    protected void tearDown() throws Exception {
        super.tearDown();
        MockContextFactory.revertSetAsInitial();
    }

    public void testPut() {
        UserDao userDao = factory.create(UserDao.class);
        TestUserBean testUserBean = new TestUserBean();
        testUserBean.setId(1);
        testUserBean.setName("new name");
        userDao.updateUser(testUserBean);
    }

    public void testGet() {
        UserDao userDao = factory.create(UserDao.class);
        TestUserBean testUserBean = new TestUserBean();
        testUserBean.setId(1);
        TestUserBean res = userDao.getUser(testUserBean);
    }

    public void testMultiGet() {
        UserDao userDao = factory.create(UserDao.class);
        List<Integer> list = new ArrayList<Integer>();
        list.add(1);
        Map<Integer, TestUserBean> res = userDao.getUserList(list);
    }
}
