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

import com.mockrunner.jdbc.BasicJDBCTestCaseAdapter;
import com.mockrunner.jdbc.PreparedStatementResultSetHandler;
import com.mockrunner.mock.jdbc.MockConnection;
import com.mockrunner.mock.jdbc.MockResultSet;
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

    private void createResultSet(Object... data) {
        MockConnection connection = getJDBCMockObjectFactory().getMockConnection();
        PreparedStatementResultSetHandler handler = connection.getPreparedStatementResultSetHandler();
        MockResultSet rs = handler.createResultSet();
        for (int i = 0; i < data.length; i++) {
            Object colName = data[i++];
            Object colValues = data[i];
            rs.addColumn(colName.toString(), (Object[]) colValues);
        }
        handler.prepareGlobalResultSet(rs);
    }

    public void testPut() {
        UserDao userDao = factory.create(UserDao.class);
        TestUserBean testUserBean = new TestUserBean(true);
        testUserBean.setId(1);
        testUserBean.setName("new name");
        userDao.updateUser(testUserBean);
    }

    public void testGet() {
        createResultSet("id", new Object[]{1}, "name", new Object[]{"user1"});
        UserDao userDao = factory.create(UserDao.class);
        TestUserBean testUserBean = new TestUserBean(true);
        testUserBean.setId(1);
        TestUserBean res = userDao.getUser(testUserBean);
    }

    public void testMultiGet() {
        createResultSet("id", new Object[]{1}, "name", new Object[]{"user1"});
        UserDao userDao = factory.create(UserDao.class);
        List<Integer> list = new ArrayList<Integer>();
        list.add(1);
        Map<Integer, TestUserBean> res = userDao.getUserList(list);
    }
}
