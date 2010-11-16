package com.sf.ddao.orm.rsmapper;

import com.sf.ddao.orm.RSMapper;
import com.sf.ddao.orm.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by psyrtsov
 */
public class SingleRowRSMapper implements RSMapper {
    private final RowMapper rowMapper;

    public SingleRowRSMapper(RowMapper rowMapper) {
        this.rowMapper = rowMapper;
    }

    public Object handle(ResultSet rs) throws SQLException {
        return rs.next() ? rowMapper.map(rs) : null;
    }
}
