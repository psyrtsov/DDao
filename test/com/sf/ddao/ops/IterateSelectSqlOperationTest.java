package com.sf.ddao.ops;

import com.mockrunner.jdbc.BasicJDBCTestCaseAdapter;
import com.mockrunner.jdbc.PreparedStatementResultSetHandler;
import com.mockrunner.mock.jdbc.MockConnection;
import com.mockrunner.mock.jdbc.MockResultSet;
import com.sf.ddao.*;
import com.sf.ddao.alinker.ALinker;
import org.mockejb.jndi.MockContextFactory;

import java.util.List;

/**
 * Date: Feb 8, 2010
 * Time: 5:56:11 PM
 */
public class IterateSelectSqlOperationTest extends BasicJDBCTestCaseAdapter {
    ALinker aLinker;
    private static final Integer[] TYPE_LIST = {1, 2, 3};

    @JDBCDao(value = "jdbc://test", driver = "com.mockrunner.mock.jdbc.MockDriver")
    public static interface TestDao extends TransactionableDao {
        @IterateSelect("select id, name from user where id = #id# and type = #1#")
        List<TestUserBean> getUser(TestUserBean userBean, @IterableArg Integer[] typeList);

    }

    protected void setUp() throws Exception {
        super.setUp();
        aLinker = new ALinker();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
        MockContextFactory.revertSetAsInitial();
        aLinker = null;
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
        TestDao dao = aLinker.create(TestDao.class, null);

        // ruse it for multiple invocations
        getUserOnce(dao, 1, "foo", true);
        getUserOnce(dao, 2, "bar", true);
    }

    private void getUserOnce(TestDao dao, int id, String name, boolean connShouldBeClosed) {
        // setup test
        TestUserBean data = new TestUserBean();
        data.setId(id);
        data.setName(name);
        createResultSet("id", new Object[]{data.getId()}, "name", new Object[]{data.getName()});

        // execute dao method
        List<TestUserBean> resList = dao.getUser(data, TYPE_LIST);

        // verify result
        assertNotNull(resList);
        assertEquals(resList.size(), TYPE_LIST.length);
        for (TestUserBean res : resList) {
            assertEquals(res.getId(), data.getId());
            assertEquals(res.getName(), data.getName());
        }

        verifySQLStatementExecuted("select id, name from user where id = ? and type = ?");
        for (int i = 0; i < TYPE_LIST.length; i++) {
            int type = TYPE_LIST[i];
            Object param = getPreparedStatementParameter(i, 2);
            assertEquals(type, param);
        }
        verifyAllResultSetsClosed();
        verifyAllStatementsClosed();
        if (connShouldBeClosed) {
            verifyConnectionClosed();
        }
    }


}
