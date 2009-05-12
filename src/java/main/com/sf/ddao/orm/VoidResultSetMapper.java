package com.sf.ddao.orm;

import java.sql.ResultSet;

/**
 * Created by Pavel Syrtsov
 * Date: May 12, 2009
 */
public class VoidResultSetMapper implements ResultSetMapper {
    @Override
    public boolean addRecord(ResultSet resultSet) throws Exception {
        return true;
    }

    @Override
    public Object getResult() {
        return null;
    }
}
