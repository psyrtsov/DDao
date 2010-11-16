package com.sf.ddao.orm.rsmapper.rowmapper;

import com.sf.ddao.orm.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 */
public class SelfRowMapper implements RowMapper {
    private final Class<? extends RowMapper> resultClass;

    public SelfRowMapper(Class<? extends RowMapper> resultClass) {
        this.resultClass = resultClass;
    }

    public Object map(ResultSet rs) throws SQLException {
        RowMapper result;
        try {
            result = resultClass.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return result.map(rs);
    }
}
