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

package com.sf.ddao.shards;

import com.mockrunner.jdbc.JDBCTestModule;
import com.mockrunner.jdbc.PreparedStatementResultSetHandler;
import com.mockrunner.mock.jdbc.JDBCMockObjectFactory;
import com.mockrunner.mock.jdbc.MockResultSet;
import com.sf.ddao.Delete;
import com.sf.ddao.InsertAndGetGeneratedKey;
import com.sf.ddao.TestUserBean;
import com.sf.ddao.alinker.ALinker;
import junit.framework.TestCase;
import org.mockejb.jndi.MockContextFactory;

import java.math.BigDecimal;

import static com.sf.ddao.crud.CRUDDao.CRUD_DELETE;
import static com.sf.ddao.crud.CRUDDao.CRUD_INSERT;

/**
 * Created by psyrtsov
 */
public class ShardedCRUDDaoTest extends TestCase {
    ALinker factory;
    private JDBCTestModule testModule1;
    private JDBCTestModule testModule2;

    @ShardedDao(TestShardingService.class)
    public static interface TestUserDao extends ShardedCRUDDao<TestUserBean, Long> {
    }

    @ShardedDao(TestShardingService.class)
    public static interface TestUserDao1 {
        @InsertAndGetGeneratedKey(CRUD_INSERT)
        BigDecimal create(@ShardKey("id") TestUserBean bean);

        @Delete(CRUD_DELETE)
        int delete(@ShardKey("id") TestUserBean bean);
    }

    protected void setUp() throws Exception {
        factory = new ALinker();
        super.setUp();
        MockContextFactory.setAsInitial();

        JDBCMockObjectFactory mockFactory1 = new JDBCMockObjectFactory();
        testModule1 = new JDBCTestModule(mockFactory1);
        JDBCMockObjectFactory mockFactory2 = new JDBCMockObjectFactory();
        testModule2 = new JDBCTestModule(mockFactory2);

        final TestShardingService controlDao = factory.create(TestShardingService.class);
        controlDao.setDS1(mockFactory1.getMockDataSource());
        controlDao.setDS2(mockFactory2.getMockDataSource());
    }

    protected void tearDown() throws Exception {
        super.tearDown();
        MockContextFactory.revertSetAsInitial();
    }

    private void createResultSet(JDBCTestModule testModule, Object... data) {
        PreparedStatementResultSetHandler handler = testModule.getPreparedStatementResultSetHandler();
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

        final long id = 7;
        createResultSet(testModule1, "id", new Object[]{id});
        // setup test
        TestUserBean data = new TestUserBean(true);
        data.setId(id);
        data.setName("name");

        // execute dao method
        Number res = dao.create(data);

        // verify result
//        assertNotNull(res);
//        assertEquals(1, res);

        final String sql = "insert into test_user(gender,long_name,name) values(?,?,?)";
        testModule1.verifySQLStatementExecuted(sql);
        testModule1.verifyPreparedStatementParameter(sql, 1, data.getGender().name());
        testModule1.verifyPreparedStatementParameter(sql, 3, data.getName());
        testModule1.verifyAllResultSetsClosed();
        testModule1.verifyAllStatementsClosed();
        testModule1.verifyConnectionClosed();
    }

    public void testCreateWithSeparateMethod() throws Exception {
        // create dao object
        TestUserDao1 dao = factory.create(TestUserDao1.class, null);

        final long id = 7;
        createResultSet(testModule1, "id", new Object[]{id});
        // setup test
        TestUserBean data = new TestUserBean(true);
        data.setId(id);
        data.setName("name");

        // execute dao method
        Number res = dao.create(data);

        // verify result
//        assertNotNull(res);
//        assertEquals(1, res);

        final String sql = "insert into test_user(gender,long_name,name) values(?,?,?)";
        testModule1.verifySQLStatementExecuted(sql);
        testModule1.verifyPreparedStatementParameter(sql, 1, data.getGender().name());
        testModule1.verifyPreparedStatementParameter(sql, 3, data.getName());
        testModule1.verifyAllResultSetsClosed();
        testModule1.verifyAllStatementsClosed();
        testModule1.verifyConnectionClosed();
    }

    public void testRead() throws Exception {
        // create dao object
        TestUserDao dao = factory.create(TestUserDao.class, null);

        final long id = 17;
        String name = "name77";
        createResultSet(testModule2, "name", new Object[]{name});
        // setup test
        TestUserBean res = dao.read(id);

        // verify result
        assertNotNull(res);
        assertEquals(name, res.getName());

        final String sql = "select * from test_user where id=? limit 1";
        testModule2.verifySQLStatementExecuted(sql);
        testModule2.verifyPreparedStatementParameter(sql, 1, id);
        testModule2.verifyAllResultSetsClosed();
        testModule2.verifyAllStatementsClosed();
        testModule2.verifyConnectionClosed();
    }

    public void testUpdate() throws Exception {
        // create dao object
        TestUserDao dao = factory.create(TestUserDao.class, null);

        // setup test
        TestUserBean data = new TestUserBean(true);
        data.setId(17);
        data.setName("name");

        createResultSet(testModule2, "id", new Object[]{data.getId()});

        // execute dao method
        Number res = dao.update(data);

        // verify result
//        assertNotNull(res);
//        assertEquals(1, res);

        final String sql = "update test_user set gender=?,long_name=?,name=? where id=?";
        testModule2.verifySQLStatementExecuted(sql);
        testModule2.verifyPreparedStatementParameter(sql, 1, data.getGender().name());
        testModule2.verifyPreparedStatementParameter(sql, 3, data.getName());
        testModule2.verifyPreparedStatementParameter(sql, 4, data.getId());
        testModule2.verifyAllResultSetsClosed();
        testModule2.verifyAllStatementsClosed();
        testModule2.verifyConnectionClosed();
    }

    public void testDelete() throws Exception {
        // create dao object
        TestUserDao dao = factory.create(TestUserDao.class, null);

        final long id = 17;
        createResultSet(testModule2, "id", new Object[]{id});

        // execute dao method
        Number res = dao.delete(id);

        // verify result
//        assertNotNull(res);
//        assertEquals(1, res);

        final String sql = "delete from test_user where id=?";
        testModule2.verifySQLStatementExecuted(sql);
        testModule2.verifyPreparedStatementParameter(sql, 1, id);
        testModule2.verifyAllResultSetsClosed();
        testModule2.verifyAllStatementsClosed();
        testModule2.verifyConnectionClosed();
    }

    public void testDeleteWithSeparateMethod() throws Exception {
        // create dao object
        TestUserDao1 dao = factory.create(TestUserDao1.class, null);

        final long id = 17;
        TestUserBean data = new TestUserBean(true);
        data.setId(id);

        createResultSet(testModule2, "id", new Object[]{id});

        // execute dao method
        Number res = dao.delete(data);

        // verify result
//        assertNotNull(res);
//        assertEquals(1, res);

        final String sql = "delete from test_user where id=?";
        testModule2.verifySQLStatementExecuted(sql);
        testModule2.verifyPreparedStatementParameter(sql, 1, id);
        testModule2.verifyAllResultSetsClosed();
        testModule2.verifyAllStatementsClosed();
        testModule2.verifyConnectionClosed();
    }

}
