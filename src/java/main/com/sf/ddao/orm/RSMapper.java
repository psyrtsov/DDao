package com.sf.ddao.orm;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by psyrtsov
 */
public interface RSMapper {
    Object handle(ResultSet rs) throws SQLException;
}
