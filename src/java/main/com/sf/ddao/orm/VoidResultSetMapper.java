package com.sf.ddao.orm;

import java.sql.ResultSet;

/**
 * Created by Pavel Syrtsov
 * Date: May 12, 2009
 */
public class VoidResultSetMapper implements ResultSetMapper {
    public boolean addRecord(ResultSet resultSet) throws Exception {
        return true;
    }

    public Object getResult() {
        return null;
    }
}
