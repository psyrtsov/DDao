/**
 * Copyright 2008 Pavel Syrtsov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations
 * under the License.
 */

package com.sf.ddao;

import com.mockrunner.jdbc.JDBCTestModule;
import com.mockrunner.jdbc.PreparedStatementResultSetHandler;
import com.mockrunner.mock.jdbc.JDBCMockObjectFactory;
import com.mockrunner.mock.jdbc.MockResultSet;
import com.sf.ddao.alinker.ALinker;
import com.sf.ddao.alinker.FactoryException;
import com.sf.ddao.alinker.initializer.InitializerException;
import com.sf.ddao.conn.JNDIDataSourceHandler;
import com.sf.ddao.factory.param.ThreadLocalParameter;
import com.sf.ddao.shards.ShardKey;
import com.sf.ddao.shards.ShardedJNDIDao;
import junit.framework.TestCase;
import org.mockejb.jndi.MockContextFactory;

import javax.naming.InitialContext;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by: Pavel Syrtsov
 * Date: Apr 6, 2007
 * Time: 7:00:11 PM
 */
public class ShardedJNDIDaoTest extends TestCase {
    ALinker factory;
    private static final String PART_NAME = "testPartName";
    private JDBCMockObjectFactory shardControlDBMockFactory1;
    private JDBCTestModule shardControlDBModule;
    private JDBCTestModule testModule1;
    private JDBCTestModule testModule2;
    private static final String SHARD1 = "jdbc/shard1";
    private static final String SHARD2 = "jdbc/shard2";

    @ShardedJNDIDao("testshardset")
    public static interface TestUserDao {
        /**
         * in this statement we assume that 1st method arg is Java Bean
         * and refer to property by name. It works same way for Map.
         *
         * @param userBean - parameter object
         * @return object created from data returned by sql
         */
        @Select("select id, name from user where id = #id#")
        TestUserBean getUser(@ShardKey("id") TestUserBean userBean);

        @Select("select id, name from user_data where user_id = #0#")
        List<TestUserBean> getUserDataList(@ShardKey int userId);

        /**
         * 1st parameter passed by reference, 2nd by value (by injecting result of toString() into SQL).
         *
         * @param tableName name of table
         * @param size      - max size of array
         * @param userId    - query parameter
         * @return objects created from data returned by sql
         */
        @Select("select id, name from $0$ where user_id = #2# limit #1#")
        TestUserBean[] getUserDataArray(String tableName, int size, @ShardKey int userId);

        @Select("select id, name from user_data where user_id = #0#")
        void processUserData(@ShardKey int userId, SelectCallback selectCallback);

        /**
         * values that have '()' assumed to be call to static function,
         * at this point we have only function that allows to pass value thrue ThreadLocal
         *
         * @param userId - query paramter
         * @return value returned by query
         */
        @Select("select id from user_data where part = '$threadLocal:" + PART_NAME + "$' and user_id = #0#")
        int getUserData(@ShardKey int userId);
    }

    protected void setUp() throws Exception {
        super.setUp();
        MockContextFactory.setAsInitial();
        InitialContext context = new InitialContext();

        shardControlDBMockFactory1 = new JDBCMockObjectFactory();
        shardControlDBModule = new JDBCTestModule(shardControlDBMockFactory1);
        JDBCMockObjectFactory mockFactory1 = new JDBCMockObjectFactory();
        testModule1 = new JDBCTestModule(mockFactory1);
        JDBCMockObjectFactory mockFactory2 = new JDBCMockObjectFactory();
        testModule2 = new JDBCTestModule(mockFactory2);
        context.rebind(JNDIDataSourceHandler.DS_CTX_PREFIX + "jdbc/shardControlDB", shardControlDBMockFactory1.getMockDataSource());
        context.rebind(JNDIDataSourceHandler.DS_CTX_PREFIX + SHARD1, mockFactory1.getMockDataSource());
        context.rebind(JNDIDataSourceHandler.DS_CTX_PREFIX + SHARD2, mockFactory2.getMockDataSource());
        setupShardData();
        this.factory = new ALinker();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
        MockContextFactory.revertSetAsInitial();
        shardControlDBMockFactory1.restoreDrivers();
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

    private void setupShardData() {
        createResultSet(shardControlDBModule, "startId", new Object[]{1, 11}, "endId", new Object[]{10, 20}, "dsName", new Object[]{SHARD1, SHARD2});
    }

    public void testSingleRecordGet() throws Exception {
        // create dao object
        TestUserDao dao = factory.create(TestUserDao.class, null);

        // reuse it for multiple invocations
        getUserOnce(testModule1, dao, 1, "foo1");
        getUserOnce(testModule1, dao, 10, "foo2");
        getUserOnce(testModule2, dao, 11, "bar1");
        getUserOnce(testModule2, dao, 20, "bar2");
    }

    private void getUserOnce(JDBCTestModule testModule, TestUserDao dao, int id, String name) throws Exception {
        // setup test
        TestUserBean data = new TestUserBean();
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
        testModule.verifyConnectionClosed();
    }

    public void testGetRecordList() throws Exception {
        TestUserDao dao = factory.create(TestUserDao.class, null);
        getUserDataList(dao, testModule1, 1);
        getUserDataList(dao, testModule1, 10);
        getUserDataList(dao, testModule2, 11);
        getUserDataList(dao, testModule2, 20);

    }

    private void getUserDataList(TestUserDao dao, JDBCTestModule testModule, int userId) {
        // setup test
        createResultSet(testModule, "id", new Object[]{1, 2}, "name", new Object[]{"foo", "bar"});

        // execute dao method
        List<TestUserBean> res = dao.getUserDataList(userId);

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

    public void testGetUserArray() throws Exception {
        // execute dao method
        TestUserDao dao = factory.create(TestUserDao.class, null);
        getUserDataArray(dao, testModule1, 1);
        getUserDataArray(dao, testModule1, 10);
        getUserDataArray(dao, testModule2, 11);
        getUserDataArray(dao, testModule2, 20);

    }

    private void getUserDataArray(TestUserDao dao, JDBCTestModule testModule, int userId) throws FactoryException, InitializerException {
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
        TestUserDao dao = factory.create(TestUserDao.class, null);
        processUserData(dao, testModule1);

    }

    private void processUserData(TestUserDao dao, JDBCTestModule testModule) {
        // setup test
        createResultSet(testModule, "id", new Object[]{1, 2}, "name", new Object[]{"foo", "bar"});
        final List<TestUserBean> res = new ArrayList<TestUserBean>();

        // execute dao method
        dao.processUserData(1, new SelectCallback<TestUserBean>() {
            public boolean processRecord(TestUserBean record) {
                res.add(record);
                return true;
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
        TestUserDao dao = factory.create(TestUserDao.class, null);
        getUserData(dao, 1, testModule1, 0);
        getUserData(dao, 10, testModule1, 1);
        getUserData(dao, 11, testModule2, 0);
        getUserData(dao, 20, testModule2, 1);

    }

    private void getUserData(TestUserDao dao, int userId, JDBCTestModule testModule, int idx) {
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
}
