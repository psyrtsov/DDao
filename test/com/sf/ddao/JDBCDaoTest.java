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

package com.sf.ddao;

import com.mockrunner.jdbc.BasicJDBCTestCaseAdapter;
import com.mockrunner.jdbc.PreparedStatementResultSetHandler;
import com.mockrunner.mock.jdbc.MockConnection;
import com.mockrunner.mock.jdbc.MockResultSet;
import com.sf.ddao.alinker.ALinker;
import com.sf.ddao.chain.UseContext;
import com.sf.ddao.factory.param.ThreadLocalParameter;
import com.sf.ddao.orm.RSMapper;
import com.sf.ddao.orm.UseRSMapper;
import com.sf.ddao.orm.rsmapper.rowmapper.BeanRowMapper;
import org.apache.commons.chain.Context;
import org.mockejb.jndi.MockContextFactory;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static com.sf.ddao.chain.CtxHelper.context;

/**
 * JDBCDaoTest is testing basic DDao functionality executed upon simple JDBC connection created by JDBC DriverManager call.
 * <p/>
 * Created by: Pavel Syrtsov
 * Date: Apr 6, 2007
 * Time: 7:00:11 PM
 */
public class JDBCDaoTest extends BasicJDBCTestCaseAdapter {
    ALinker factory;
    private static final String PART_NAME = "partName";

    @JDBCDao(value = "jdbc://test", driver = "com.mockrunner.mock.jdbc.MockDriver")
    public static interface TestUserDao extends TransactionableDao {
        /**
         * in this statement we assume that 1st method arg is Java Bean
         * and refer to property by name. It works same way for Map.
         *
         * @param userBean - parameter object
         * @return - TestUserBean created using data from SQL
         */
        @Select("select id, name from user where id = #id#")
        TestUserBean getUser(TestUserBean userBean);

        @Select("select id, name from user")
        List<TestUserBean> getUserList();

        /**
         * 1st parametterpassed by reference, 2nd by value (by injecting result of toString() into SQL).
         * added underscore to column name to verify that it gets stripped
         *
         * @param tableName - paramter object
         * @param size      - max size of return array
         * @return array of TestUserBeans created using data returned by SQL query
         */
        @Select("select id, _Name from $0$ limit #1#")
        TestUserBean[] getUserArray(String tableName, int size);

        @Select("select id, name from user")
        void processUsers(@UseRSMapper RSMapper selectCallback);

        /**
         * values that have '()' assumed to be call to static function,
         * at this point we have only function that allows to pass value thrue ThreadLocal
         *
         * @param name - query parameter
         * @return id
         */
        @Select("select id from user where part = '$ctx:" + PART_NAME + "$' and name = #0#")
        int getUserIdByName(String name, @UseContext Context ctx);

        @SelectThenInsert({"select nextval from userIdSequence", "insert into user(id,name) values(#threadLocal:id#, #name#)"})
        int addUser(TestUserBean user);

        @Insert("insert into user (id, name) values #fwd:0#")
        int insertDynamic(DynamicQuery dq);
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

    public void testSingleRecordGet() throws Exception {
        // create dao object
        TestUserDao dao = factory.create(TestUserDao.class, null);

        // ruse it for multiple invocations
        getUserOnce(dao, 1, "foo", true);
        getUserOnce(dao, 2, "bar", true);
    }

    private void getUserOnce(TestUserDao dao, int id, String name, boolean connShouldBeClosed) {
        // setup test
        TestUserBean data = new TestUserBean(true);
        data.setId(id);
        data.setName(name);
        createResultSet("id", new Object[]{data.getId()}, "name", new Object[]{data.getName()});

        // execute dao method
        TestUserBean res = dao.getUser(data);

        // verify result
        assertNotNull(res);
        assertEquals(res.getId(), data.getId());
        assertEquals(res.getName(), data.getName());

        verifySQLStatementExecuted("select id, name from user where id = ?");
        verifyAllResultSetsClosed();
        verifyAllStatementsClosed();
        if (connShouldBeClosed) {
            verifyConnectionClosed();
        }
    }

    public void testGetRecordList() throws Exception {
        // setup test
        createResultSet("id", new Object[]{1, 2}, "name", new Object[]{"foo", "bar"});

        // execute dao method
        TestUserDao dao = factory.create(TestUserDao.class, null);
        List<TestUserBean> res = dao.getUserList();

        // verify result
        assertNotNull(res);
        assertEquals(res.size(), 2);
        assertEquals(res.get(0).getId(), 1);
        assertEquals(res.get(0).getName(), "foo");
        assertEquals(res.get(1).getId(), 2);
        assertEquals(res.get(1).getName(), "bar");

        verifySQLStatementExecuted("select id, name from user");
        verifyAllResultSetsClosed();
        verifyAllStatementsClosed();
        verifyConnectionClosed();
    }

    public void testGetUserArray() throws Exception {
        // setup test
        createResultSet("id", new Object[]{1, 2}, "name", new Object[]{"foo", "bar"});

        // execute dao method
        TestUserDao dao = factory.create(TestUserDao.class, null);
        TestUserBean[] res = dao.getUserArray("user", 2);

        // verify result
        assertNotNull(res);
        assertEquals(res.length, 2);
        assertEquals(res[0].getId(), 1);
        assertEquals(res[0].getName(), "foo");
        assertEquals(res[1].getId(), 2);
        assertEquals(res[1].getName(), "bar");

        verifySQLStatementExecuted("select id, _name from user limit ?");
        verifySQLStatementParameter("select id, _name from user limit ?", 0, 1, 2);
        verifyAllResultSetsClosed();
        verifyAllStatementsClosed();
        verifyConnectionClosed();
    }

    public void testSelectCallback() throws Exception {
        // setup test
        createResultSet("id", new Object[]{1, 2}, "name", new Object[]{"foo", "bar"});
        final List<TestUserBean> res = new ArrayList<TestUserBean>();

        // execute dao method
        TestUserDao dao = factory.create(TestUserDao.class, null);
        dao.processUsers(new RSMapper() {
            BeanRowMapper rowMapper = new BeanRowMapper(TestUserBean.class);

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

        verifySQLStatementExecuted("select id, name from user");
        verifyAllResultSetsClosed();
        verifyAllStatementsClosed();
        verifyConnectionClosed();
    }

    public void testGetUserId() throws Exception {
        // setup test
        final int id = 77;
        final String testName = "testName";
        final String testPart = "testPart";
        createResultSet("id", new Object[]{id});

        // execute dao method
        TestUserDao dao = factory.create(TestUserDao.class, null);
        int res = dao.getUserIdByName(testName, context(PART_NAME, testPart));

        // verify result
        ThreadLocalParameter.remove(PART_NAME);
        assertEquals(id, res);

        verifySQLStatementExecuted("select id from user where part = '" + testPart + "' and name = ?");
        verifyPreparedStatementParameter(0, 1, testName);
        verifyAllResultSetsClosed();
        verifyAllStatementsClosed();
        verifyConnectionClosed();
    }

    public void testNullParameter() throws Exception {
        // setup test
        final int id = 77;
        final String testName = null;
        final String testPart = "testPart";
        createResultSet("id", new Object[]{id});

        // execute dao method
        TestUserDao dao = factory.create(TestUserDao.class, null);
        int res = dao.getUserIdByName(testName, context(PART_NAME, testPart));

        // verify result
        ThreadLocalParameter.remove(PART_NAME);
        assertEquals(id, res);

        verifySQLStatementExecuted("select id from user where part = '" + testPart + "' and name = ?");
        verifyPreparedStatementParameter(0, 1, testName);
        verifyAllResultSetsClosed();
        verifyAllStatementsClosed();
        verifyConnectionClosed();
    }

    public void testTx() throws Exception {
        final int id = 77;
        final String testName = "testName";

        // execute dao method
        final TestUserDao dao = factory.create(TestUserDao.class, null);
        final TestUserBean user = new TestUserBean(true);
        user.setName(testName);
        TxHelper.execInTx(dao, new Runnable() {
            public void run() {
                try {
                    createResultSet("nextval", new Object[]{id});
                    final int res = dao.addUser(user);
                    final Connection connection1 = TxHelper.getConnectionOnHold();
                    assertNotNull(connection1);
                    assertFalse(connection1.isClosed());
                    verifyNotCommitted();
                    getUserOnce(dao, 11, "user11", false);
                    final Connection connection2 = TxHelper.getConnectionOnHold();
                    assertSame(connection1, connection2);
                    assertFalse(connection2.isClosed());
                    verifyNotCommitted();
                    assertEquals(id, res);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        final Connection connection = TxHelper.getConnectionOnHold();
        assertNull(connection);
        verifyCommitted();
        verifySQLStatementExecuted("select nextval from userIdSequence");
        verifySQLStatementExecuted("insert into user(id,name) values(?, ?)");
        verifyPreparedStatementParameter(1, 1, id);
        verifyPreparedStatementParameter(1, 2, testName);
        verifyAllResultSetsClosed();
        verifyAllStatementsClosed();
        verifyConnectionClosed();
    }

    public void testDynamicQuery() throws Exception {
        // create dao object
        TestUserDao dao = factory.create(TestUserDao.class, null);

        DynamicQuery dq = new DynamicQuery();
        dq.add("(?,?)", 1, "foo");
        dq.add("(?,?)", 2, "bar");
        dao.insertDynamic(dq);

        final String sql = "insert into user (id, name) values (?,?)(?,?)";
        verifySQLStatementExecuted(sql);
        verifySQLStatementParameter(sql, 0, 1, 1);
        verifySQLStatementParameter(sql, 0, 2, "foo");
        verifySQLStatementParameter(sql, 0, 3, 2);
        verifySQLStatementParameter(sql, 0, 4, "bar");
        verifyAllResultSetsClosed();
        verifyAllStatementsClosed();
        verifyConnectionClosed();
    }

}
