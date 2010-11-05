package com.sf.ddao.astore.impl;

import com.sf.ddao.conn.ConnectionHandlerHelper;
import com.sf.ddao.factory.param.ParameterException;
import com.sf.ddao.factory.param.ParameterHelper;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by psyrtsov
 */
public class DBPutCommand implements Serializable, Command {
    private static final long serialVersionUID = 4584656621930746876L;
    private String sql;
    private List<Object> paramData;

    public DBPutCommand() {
    }

    public DBPutCommand(String sql, List<Object> paramData) {
        this.sql = sql;
        this.paramData = paramData;
    }

    public boolean execute(Context context) throws Exception {
        PreparedStatement preparedStatement = prepareStatement(context);
        preparedStatement.executeUpdate();
        preparedStatement.close();
        return CONTINUE_PROCESSING;
    }

    public PreparedStatement prepareStatement(Context context) throws SQLException, ParameterException {
        final Connection connection = ConnectionHandlerHelper.getConnection(context);
        PreparedStatement preparedStatement;
        preparedStatement = connection.prepareStatement(sql);
        int i = 1;
        for (Object parameter : paramData) {
            ParameterHelper.bind(preparedStatement, i++, parameter);
        }
        return preparedStatement;
    }
}
