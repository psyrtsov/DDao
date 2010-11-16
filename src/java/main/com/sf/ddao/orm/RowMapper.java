package com.sf.ddao.orm;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by psyrtsov
 */
public interface RowMapper {
    Object map(ResultSet rs) throws SQLException;
}
