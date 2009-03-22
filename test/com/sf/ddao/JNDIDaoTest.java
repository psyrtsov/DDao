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

import com.mockrunner.jdbc.BasicJDBCTestCaseAdapter;
import com.mockrunner.jdbc.PreparedStatementResultSetHandler;
import com.mockrunner.mock.jdbc.JDBCMockObjectFactory;
import com.mockrunner.mock.jdbc.MockDataSource;
import com.mockrunner.mock.jdbc.MockResultSet;
import com.sf.ddao.alinker.ALinker;
import com.sf.ddao.conn.JNDIDataSourceHandler;
import com.sf.ddao.factory.param.ThreadLocalStatementParameter;
import org.mockejb.jndi.MockContextFactory;

import javax.naming.InitialContext;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by: Pavel Syrtsov
 * Date: Apr 6, 2007
 * Time: 7:00:11 PM
 */
public class JNDIDaoTest extends BasicJDBCTestCaseAdapter {
    ALinker factory;
    private static final String PART_NAME = "partName";

    @JNDIDao("jdbc/testdb")
    public static interface TestUserDao {
        /**
         * in this statement we assume that 1st method arg is Java Bean
         * and refer to property by name. It works same way for Map.
         * @param userBean - properties of this java bean used in the query as parameters
         * @return - query result transformed into java bean, since we defined only one bean as result
         * of this method, we expect query to return only one row.
         */
        @Select("select id, name from user where id = #id#")
        TestUserBean getUser(TestUserBean userBean);

        @Select("select id, name from user")
        List<TestUserBean> getUserList();

        /**
         * 2nd by value (by injecting result of toString() into SQL).
         * @param tableName - reffered in query text by enclosing prameter number in dollar sign ('$0$'),
         * it means parametter passed by binding it as JDBC prepared statement parameter.
         * @param size - reffered in query text by enclosing parameter number in pound sign ('#1#'),
         * it means parameter's value will be injected
         * @return - array of TestUserBeans built from data returned by SQL 
         */
        @Select("select id, name from $0$ limit #1#")
        TestUserBean[] getUserArray(String tableName, int size);

        @Select("select id, name from user")
        void processUsers(SelectCallback selectCallback);

        /**
         * values that have '()' assumed to be call to static function,
         * at this point we have only function that allows to pass value thrue ThreadLocal
         * @param name - name
         * @return id
         */
        @Select("select id from user where part = '$global(" + PART_NAME + ")$' and name = #0#")
        int getUserIdByName(String name);
    }

    protected void setUp() throws Exception {
        super.setUp();
        JDBCMockObjectFactory factory = getJDBCMockObjectFactory();
        MockDataSource ds = factory.getMockDataSource();
        MockContextFactory.setAsInitial();
        InitialContext context = new InitialContext();
        context.rebind(JNDIDataSourceHandler.DS_CTX_PREFIX + "jdbc/testdb", ds);
        this.factory = new ALinker();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
        MockContextFactory.revertSetAsInitial();
    }

    private void createResultSet(Object... data) {
        PreparedStatementResultSetHandler handler = getPreparedStatementResultSetHandler();
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
        getUserOnce(dao, 1, "foo");
        getUserOnce(dao, 2, "bar");
    }

    private void getUserOnce(TestUserDao dao, int id, String name) throws Exception {
        // setup test
        TestUserBean data = new TestUserBean();
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
        verifyConnectionClosed();
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

        verifySQLStatementExecuted("select id, name from user limit ?");
        verifySQLStatementParameter("select id, name from user limit ?", 0, 1, 2);
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
        dao.processUsers(new SelectCallback<TestUserBean>() {
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
        ThreadLocalStatementParameter.put(PART_NAME, testPart);

        // execute dao method
        TestUserDao dao = factory.create(TestUserDao.class, null);
        int res = dao.getUserIdByName(testName);

        // verify result
        ThreadLocalStatementParameter.remove(PART_NAME);
        assertEquals(id, res);

        verifySQLStatementExecuted("select id from user where part = '" + testPart + "' and name = ?");
        verifyPreparedStatementParameter(0, 1, testName);
        verifyAllResultSetsClosed();
        verifyAllStatementsClosed();
        verifyConnectionClosed();
    }
}