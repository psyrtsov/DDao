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
import com.sf.ddao.orm.RSMapper;
import com.sf.ddao.orm.UseRSMapper;
import com.sf.ddao.orm.rsmapper.rowmapper.BeanRowMapper;
import org.mockejb.jndi.MockContextFactory;

import javax.naming.InitialContext;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by: Pavel Syrtsov
 * Date: Apr 6, 2007
 * Time: 7:00:11 PM
 */
public class UseStatementFactoryTest extends BasicJDBCTestCaseAdapter {

    @JNDIDao("jdbc/testdb")
    public static interface UserDao {
        @Select("select id, name from user where id = $0$")
        TestUserBean getUser(int id);

        @Select("select id, name from user")
        List<TestUserBean> getUserList();

        @Select("select id, name from user limit $0$")
        TestUserBean[] getUserArray(int size);

        @Select("select id, name from user")
        void processUsers(@UseRSMapper RSMapper selectCallback);
    }

    ALinker aLinker;

    protected void setUp() throws Exception {
        super.setUp();
        JDBCMockObjectFactory factory = getJDBCMockObjectFactory();
        MockDataSource ds = factory.getMockDataSource();
        MockContextFactory.setAsInitial();
        InitialContext context = new InitialContext();
        context.rebind(JNDIDataSourceHandler.DS_CTX_PREFIX + "jdbc/testdb", ds);
        aLinker = new ALinker();
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

    public void testGetUser() throws Exception {
        UserDao dao = aLinker.create(UserDao.class, null);
        createResultSet("id", new Object[]{1}, "name", new Object[]{"foobar"});
        TestUserBean res = dao.getUser(1);
        assertNotNull(res);
        assertEquals(res.getId(), 1);
        assertEquals(res.getName(), "foobar");

        verifySQLStatementExecuted("select id, name from user where id = 1");
        verifyAllResultSetsClosed();
        verifyAllStatementsClosed();
        verifyConnectionClosed();
    }

    public void testGetUserList() throws Exception {
        UserDao dao = aLinker.create(UserDao.class, null);
        createResultSet("id", new Object[]{1, 2}, "name", new Object[]{"foo", "bar"});
        List<TestUserBean> res = dao.getUserList();
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
        UserDao dao = aLinker.create(UserDao.class, null);
        createResultSet("id", new Object[]{1, 2}, "name", new Object[]{"foo", "bar"});
        TestUserBean[] res = dao.getUserArray(2);
        assertNotNull(res);
        assertEquals(res.length, 2);
        assertEquals(res[0].getId(), 1);
        assertEquals(res[0].getName(), "foo");
        assertEquals(res[1].getId(), 2);
        assertEquals(res[1].getName(), "bar");

        verifySQLStatementExecuted("select id, name from user limit 2");
        verifyAllResultSetsClosed();
        verifyAllStatementsClosed();
        verifyConnectionClosed();
    }

    public void testSelectCallback() throws Exception {
        UserDao dao = aLinker.create(UserDao.class, null);
        createResultSet("id", new Object[]{1, 2}, "name", new Object[]{"foo", "bar"});
        final List<TestUserBean> res = new ArrayList<TestUserBean>();
        dao.processUsers(new RSMapper() {
            BeanRowMapper rowMapper = new BeanRowMapper(TestUserBean.class);

            public Object handle(ResultSet rs) throws SQLException {
                while (rs.next()) {
                    res.add((TestUserBean) rowMapper.map(rs));
                }
                return null;

            }
        });
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
}
