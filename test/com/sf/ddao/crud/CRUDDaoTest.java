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

package com.sf.ddao.crud;

import com.mockrunner.jdbc.BasicJDBCTestCaseAdapter;
import com.mockrunner.jdbc.PreparedStatementResultSetHandler;
import com.mockrunner.mock.jdbc.MockConnection;
import com.mockrunner.mock.jdbc.MockResultSet;
import com.sf.ddao.JDBCDao;
import com.sf.ddao.TestUserBean;
import com.sf.ddao.alinker.ALinker;
import org.mockejb.jndi.MockContextFactory;

/**
 * Created by psyrtsov
 */
public class CRUDDaoTest extends BasicJDBCTestCaseAdapter {
    ALinker factory;

    @JDBCDao(value = "jdbc://test", driver = "com.mockrunner.mock.jdbc.MockDriver")
    public static interface TestUserDao extends CRUDDao<TestUserBean>, UpdateCallbackDao<TestUserBean> {
    }

    protected void setUp() throws Exception {
        super.setUp();
        factory = new ALinker();
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

    public void testCreate() throws Exception {
        // create dao object
        TestUserDao dao = factory.create(TestUserDao.class, null);

        final long id = 77;
        createResultSet("id", new Object[]{id});
        // setup test
        TestUserBean data = new TestUserBean(true);
        data.setName("name");

        // execute dao method
        Number res = dao.create(data);

        // verify result
//        assertNotNull(res);
//        assertEquals(1, res);

        final String sql = "insert into test_user(long_name,name) values(?,?)";
        verifySQLStatementExecuted(sql);
        verifyPreparedStatementParameter(sql, 2, data.getName());
        verifyAllResultSetsClosed();
        verifyAllStatementsClosed();
        verifyConnectionClosed();
    }

    public void testRead() throws Exception {
        // create dao object
        TestUserDao dao = factory.create(TestUserDao.class, null);

        final long id = 77;
        String name = "name77";
        createResultSet("name", new Object[]{name});
        // setup test
        TestUserBean res = dao.read(id);

        // verify result
        assertNotNull(res);
        assertEquals(name, res.getName());

        final String sql = "select * from test_user where id=? limit 1";
        verifySQLStatementExecuted(sql);
        verifyPreparedStatementParameter(sql, 1, id);
        verifyAllResultSetsClosed();
        verifyAllStatementsClosed();
        verifyConnectionClosed();
    }

    public void testUpdate() throws Exception {
        // create dao object
        TestUserDao dao = factory.create(TestUserDao.class, null);

        // setup test
        TestUserBean data = new TestUserBean(true);
        data.setId(77);
        data.setName("name");

        createResultSet("id", new Object[]{data.getId()});

        // execute dao method
        Number res = dao.update(data);

        // verify result
//        assertNotNull(res);
//        assertEquals(1, res);

        final String sql = "update test_user set long_name=?,name=? where id=?";
        verifySQLStatementExecuted(sql);
        verifyPreparedStatementParameter(sql, 2, data.getName());
        verifyPreparedStatementParameter(sql, 3, data.getId());
        verifyAllResultSetsClosed();
        verifyAllStatementsClosed();
        verifyConnectionClosed();
    }

    public void testDelete() throws Exception {
        // create dao object
        TestUserDao dao = factory.create(TestUserDao.class, null);

        final long id = 77;
        createResultSet("id", new Object[]{id});

        // execute dao method
        Number res = dao.delete(id);

        // verify result
//        assertNotNull(res);
//        assertEquals(1, res);

        final String sql = "delete from test_user where id=?";
        verifySQLStatementExecuted(sql);
        verifyPreparedStatementParameter(sql, 1, id);
        verifyAllResultSetsClosed();
        verifyAllStatementsClosed();
        verifyConnectionClosed();
    }

    public void testReadWithCallbackThenUpdate() throws Exception {
        // create dao object
        TestUserDao dao = factory.create(TestUserDao.class, null);

        final long id = 77;
        String name = "name77";
        String longName = "longName77";
        createResultSet("id", new Object[]{id}, "name", new Object[]{name}, "longName", new Object[]{longName});
        // setup test
        ResultHolderUpdateCallback<TestUserBean> updateCallback = new ResultHolderUpdateCallback<TestUserBean>();
        dao.update(id, updateCallback);
        TestUserBean res = updateCallback.getRes();

        // verify result
        assertNotNull(res);
        assertEquals(name, res.getName());

        final String selectSql = "select * from test_user where id=? limit 1";
        verifySQLStatementExecuted(selectSql);
        verifyPreparedStatementParameter(selectSql, 1, id);

        final String updateSql = "update test_user set long_name=?,name=? where id=?";
        verifySQLStatementExecuted(updateSql);
        verifyPreparedStatementParameter(updateSql, 1, longName);
        verifyPreparedStatementParameter(updateSql, 2, name);
        verifyPreparedStatementParameter(updateSql, 3, id);

        verifyAllResultSetsClosed();
        verifyAllStatementsClosed();
        verifyConnectionClosed();
    }

    private class ResultHolderUpdateCallback<T> implements UpdateCallback<T> {
        private T res;

        public void update(T bean) {
            res = bean;
        }

        public T getRes() {
            return res;
        }
    }

}
