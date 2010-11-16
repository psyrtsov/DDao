package com.sf.ddao.orm;

import java.sql.ResultSet;

/**
 * Created by psyrtsov
 */
public class ParameterBasedRSMapperFactory implements RSMapperFactory {
    private final int paramIdx;

    public ParameterBasedRSMapperFactory(int paramIdx) {
        this.paramIdx = paramIdx;
    }

    public RSMapper getInstance(Object[] args, ResultSet resultSet) {
        return (RSMapper) args[paramIdx];
    }
}
