package com.sf.ddao.orm.mapper;

import com.sf.ddao.orm.ResultSetMapper;
import com.sf.ddao.orm.ResultSetMapperException;

import java.sql.ResultSet;

/**
 *
 */
public class SelfMappingMapper implements ResultSetMapper {
    private final Class<? extends SelfMapping> resultClass;
    private SelfMapping result = null;

    public SelfMappingMapper(Class<? extends SelfMapping> resultClass) {
        this.resultClass = resultClass;
    }

    public boolean addRecord(ResultSet resultSet) throws Exception {
        if (result != null) {
            throw new ResultSetMapperException("Expected only one record for result type " + resultClass);
        }
        result = resultClass.newInstance();
        result.set(resultSet);
        return true;
    }

    public Object getResult() {
        try {
            return result;
        } finally {
            result = null;
        }
    }
}
