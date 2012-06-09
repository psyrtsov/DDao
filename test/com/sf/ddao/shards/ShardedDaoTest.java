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

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.mockrunner.jdbc.JDBCTestModule;
import com.mockrunner.jdbc.PreparedStatementResultSetHandler;
import com.mockrunner.mock.jdbc.JDBCMockObjectFactory;
import com.mockrunner.mock.jdbc.MockResultSet;
import com.sf.ddao.*;
import com.sf.ddao.chain.ChainModule;
import com.sf.ddao.factory.param.ThreadLocalParameter;
import com.sf.ddao.orm.RSMapper;
import com.sf.ddao.orm.UseRSMapper;
import com.sf.ddao.orm.rsmapper.rowmapper.BeanRowMapperFactory;
import com.sf.ddao.orm.rsmapper.rowmapper.RowMapper;
import junit.framework.TestCase;
import org.apache.commons.chain.Context;
import org.mockejb.jndi.MockContextFactory;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by: Pavel Syrtsov
 * Date: Apr 6, 2007
 * Time: 7:00:11 PM
 */
public class ShardedDaoTest extends TestCase {
    Injector injector;
    private static final String PART_NAME = "testPartName";
    private JDBCTestModule testModule1;
    private JDBCTestModule testModule2;

    @ShardedDao(TestShardingService.class)
    public static interface TestUserDao extends TransactionableDao {
        /**
         * in this statement we assume that 1st method arg is Java Bean
         * and refer to property by name. It works same way for Map.
         *
         * @param userBean - parameter object
         * @return object created from data returned by sql
         */
        @Select("select id, name from user where id = #id#")
        TestUserBean getUser(@ShardKey("id") TestUserBean userBean);

        /**
         * MultiShardSelect annotation allows to execute SQl statement
         * on multiple shards and takes care of merging results from multiple shards
         * by default it assumes that result is a collection of objects and will do merge of collections.
         * To provide custom merger logic annotation allows to define value for resultMerger class.
         *
         * @param userIdList
         * @return merged
         */
        @MultiShardSelect("select id, name from user_data where user_id in ($ctx:keyList$)")
        List<TestUserBean> getUserDataList(@ShardKey List<Integer> userIdList);

        /**
         * 1st parameter passed by reference, 2nd by value (by injecting result of toString() into SQL).
         *
         * @param tableName name of table
         * @param size      - max size of array
         * @param userId    - query parameter
         * @return objects created from data returned by sql
         */
        @Select("select id, name from $0$ where user_id = #2# limit #1#")
        TestUserBean[] getUserDataArray(String tableName, int size, @ShardKey long userId);

        @Select("select id, name from user_data where user_id = #0#")
        void processUserData(@ShardKey long userId, @UseRSMapper RSMapper selectCallback);

        /**
         * values that have ':' with prefix assumed to be call to predefined static function registered by ParameterService,
         * there are few if them predefined:
         * prefix threadLocal: allows to pass value using ThreadLocal
         * prefix ctx: allows to pass value using Context object in method arguments
         * prefix joinList: allows to join list of keys in comma separated string
         *
         * @param userId - query paramter
         * @return value returned by query
         */
        @Select("select id from user_data where part = '$threadLocal:" + PART_NAME + "$' and user_id = #0#")
        int getUserData(@ShardKey long userId);

        @SelectThenInsert({"select nextval from userIdSequence", "insert into user(id,name) values(#threadLocal:id#, #name#)"})
        long addUser(@ShardKey("id") TestUserBean user);

    }

    protected void setUp() throws Exception {
        this.injector = Guice.createInjector(new ChainModule(TestUserDao.class));
        super.setUp();
        MockContextFactory.setAsInitial();

        JDBCMockObjectFactory mockFactory1 = new JDBCMockObjectFactory();
        testModule1 = new JDBCTestModule(mockFactory1);
        JDBCMockObjectFactory mockFactory2 = new JDBCMockObjectFactory();
        testModule2 = new JDBCTestModule(mockFactory2);

        final TestShardingService controlDao = injector.getInstance(TestShardingService.class);
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


    public void testSingleRecordGet() throws Exception {
        // create dao object
        TestUserDao dao = injector.getInstance(TestUserDao.class);

        // reuse it for multiple invocations
        getUserOnce(testModule1, dao, 1, "foo1", false);
        getUserOnce(testModule1, dao, 10, "foo2", false);
        getUserOnce(testModule2, dao, 11, "bar1", false);
        getUserOnce(testModule2, dao, 20, "bar2", false);
    }

    private void getUserOnce(JDBCTestModule testModule, TestUserDao dao, int id, String name, boolean inTx) throws Exception {
        // setup test
        TestUserBean data = new TestUserBean(true);
        data.setId(id);
        data.setName(name);
        createResultSet(testModule, "id", new Object[]{data.getId()}, "name", new Object[]{data.getName()});

        // execute dao method
        TestUserBean res = dao.getUser(data);

        // verify result
        assertNotNull(res);
        assertEquals(res.getId(), data.getId());
        assertEquals(res.getName(), data.getName());

        testModule.verifySQLStatementExecuted("select id, name from user where id = ?");
        testModule.verifyAllResultSetsClosed();
        testModule.verifyAllStatementsClosed();
        if (!inTx) {
            testModule.verifyConnectionClosed();
        }
    }

    public void testMultiShardGetRecordList() throws Exception {
        TestUserDao dao = injector.getInstance(TestUserDao.class);
        // setup test
        createResultSet(testModule1, "id", new Object[]{1, 2}, "name", new Object[]{"u1", "u2"});
        createResultSet(testModule2, "id", new Object[]{15, 16}, "name", new Object[]{"u15", "u16"});

        List<Integer> userIdList = new ArrayList<Integer>();
        userIdList.add(1);
        userIdList.add(2);
        userIdList.add(15);
        userIdList.add(16);
        // execute dao method
        List<TestUserBean> res = dao.getUserDataList(userIdList);

        Collections.sort(res, new Comparator<TestUserBean>() {
            public int compare(TestUserBean testUserBean, TestUserBean testUserBean1) {
                return (int) (testUserBean.getId() - testUserBean1.getId());
            }
        });

        // verify result
        assertNotNull(res);
        assertEquals(4, res.size());

        assertEquals(1, res.get(0).getId());
        assertEquals("u1", res.get(0).getName());
        assertEquals(2, res.get(1).getId());
        assertEquals("u2", res.get(1).getName());

        assertEquals(15, res.get(2).getId());
        assertEquals("u15", res.get(2).getName());
        assertEquals(16, res.get(3).getId());
        assertEquals("u16", res.get(3).getName());

        testModule1.verifySQLStatementExecuted("select id, name from user");
        testModule1.verifyAllResultSetsClosed();
        testModule1.verifyAllStatementsClosed();
        testModule1.verifyConnectionClosed();

        testModule2.verifySQLStatementExecuted("select id, name from user");
        testModule2.verifyAllResultSetsClosed();
        testModule2.verifyAllStatementsClosed();
        testModule2.verifyConnectionClosed();
    }

    public void testGetUserArray() throws Exception {
        // execute dao method
        TestUserDao dao = injector.getInstance(TestUserDao.class);
        getUserDataArray(dao, testModule1, 1);
        getUserDataArray(dao, testModule1, 10);
        getUserDataArray(dao, testModule2, 11);
        getUserDataArray(dao, testModule2, 20);

    }

    private void getUserDataArray(TestUserDao dao, JDBCTestModule testModule, int userId) {
        // setup test
        createResultSet(testModule, "id", new Object[]{1, 2}, "name", new Object[]{"foo", "bar"});

        TestUserBean[] res = dao.getUserDataArray("user", 2, userId);

        // verify result
        assertNotNull(res);
        assertEquals(res.length, 2);
        assertEquals(res[0].getId(), 1);
        assertEquals(res[0].getName(), "foo");
        assertEquals(res[1].getId(), 2);
        assertEquals(res[1].getName(), "bar");

        testModule.verifySQLStatementExecuted("select id, name from user where user_id = ? limit ?");
        testModule.verifySQLStatementParameter("select id, name from user where user_id = ? limit ?", 0, 2, 2);
        testModule.verifyAllResultSetsClosed();
        testModule.verifyAllStatementsClosed();
        testModule.verifyConnectionClosed();
    }

    public void testSelectCallback() throws Exception {
        TestUserDao dao = injector.getInstance(TestUserDao.class);
        processUserData(dao, testModule1);

    }

    private void processUserData(TestUserDao dao, JDBCTestModule testModule) {
        // setup test
        createResultSet(testModule, "id", new Object[]{1, 2}, "name", new Object[]{"foo", "bar"});
        final List<TestUserBean> res = new ArrayList<TestUserBean>();

        // execute dao method
        dao.processUserData(1, new RSMapper() {
            RowMapper rowMapper = new BeanRowMapperFactory(TestUserBean.class).get();

            public Object handle(Context context, ResultSet rs) throws SQLException {
                while (rs.next()) {
                    res.add((TestUserBean) rowMapper.map(rs));
                }
                return null;

            }
        });

        // verify result
        assertNotNull(res);
        assertEquals(res.size(), 2);
        assertEquals(res.get(0).getId(), 1);
        assertEquals(res.get(0).getName(), "foo");
        assertEquals(res.get(1).getId(), 2);
        assertEquals(res.get(1).getName(), "bar");

        testModule.verifySQLStatementExecuted("select id, name from user");
        testModule.verifyAllResultSetsClosed();
        testModule.verifyAllStatementsClosed();
        testModule.verifyConnectionClosed();
    }

    public void testUsingStaticFunction() throws Exception {
        TestUserDao dao = injector.getInstance(TestUserDao.class);
        getUserData(dao, 1, testModule1, 0);
        getUserData(dao, 10, testModule1, 1);
        getUserData(dao, 11, testModule2, 0);
        getUserData(dao, 20, testModule2, 1);

    }

    private void getUserData(TestUserDao dao, long userId, JDBCTestModule testModule, int idx) {
        // setup test
        final int id = 11;
        final String testPart = "testPart";
        createResultSet(testModule, "id", new Object[]{id});
        ThreadLocalParameter.put(PART_NAME, testPart);

        // execute dao method
        int res = dao.getUserData(userId);

        // verify result
        ThreadLocalParameter.remove(PART_NAME);
        assertEquals(id, res);

        testModule.verifySQLStatementExecuted("select id from user_data where part = '" + testPart + "' and user_id = ?");
        testModule.verifyPreparedStatementParameter(idx, 1, userId);
        testModule.verifyAllResultSetsClosed();
        testModule.verifyAllStatementsClosed();
        testModule.verifyConnectionClosed();
    }

    public void testTx() throws Exception {
        final long id = 7;
        final String testName = "testName";

        // execute dao method
        final TestUserDao dao = injector.getInstance(TestUserDao.class);
        final TestUserBean user = new TestUserBean(true);
        user.setName(testName);
        TxHelper.execInTx(dao, new Runnable() {
            public void run() {
                try {
                    createResultSet(testModule1, "nextval", new Object[]{id});
                    final long res = dao.addUser(user);
                    final Connection connection1 = TxHelper.getConnectionOnHold();
                    assertNotNull(connection1);
                    assertFalse(connection1.isClosed());
                    testModule1.verifyNotCommitted();
                    getUserOnce(testModule1, dao, 11, "user11", true);
                    final Connection connection2 = TxHelper.getConnectionOnHold();
                    assertSame(connection1, connection2);
                    assertFalse(connection2.isClosed());
                    testModule1.verifyNotCommitted();
                    assertEquals(id, res);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }, id);
        final Connection connection = TxHelper.getConnectionOnHold();
        assertNull(connection);
        testModule1.verifyCommitted();
        testModule1.verifySQLStatementExecuted("select nextval from userIdSequence");
        testModule1.verifySQLStatementExecuted("insert into user(id,name) values(?, ?)");
        testModule1.verifyPreparedStatementParameter(1, 1, id);
        testModule1.verifyPreparedStatementParameter(1, 2, testName);
        testModule1.verifyAllResultSetsClosed();
        testModule1.verifyAllStatementsClosed();
        testModule1.verifyConnectionClosed();
    }

}
