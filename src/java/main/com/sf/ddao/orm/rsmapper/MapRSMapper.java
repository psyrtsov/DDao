package com.sf.ddao.orm.rsmapper;

import com.sf.ddao.orm.RSMapper;
import com.sf.ddao.orm.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by psyrtsov
 */
public class MapRSMapper implements RSMapper {
    private final RowMapper keyMapper;
    private final RowMapper valueMapper;

    public MapRSMapper(RowMapper keyMapper, RowMapper valueMapper) {
        this.keyMapper = keyMapper;
        this.valueMapper = valueMapper;
    }

    public Object handle(ResultSet rs) throws SQLException {
        Map res = new HashMap();
        while (rs.next()) {
            Object key = keyMapper.map(rs);
            Object value = valueMapper.map(rs);
            //noinspection unchecked
            res.put(key, value);
        }
        return res;
    }
}
