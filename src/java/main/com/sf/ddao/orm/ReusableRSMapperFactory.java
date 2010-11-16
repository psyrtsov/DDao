package com.sf.ddao.orm;

import java.sql.ResultSet;

/**
 * Created by psyrtsov
 */
public class ReusableRSMapperFactory implements RSMapperFactory {
    private final RSMapper instance;

    public ReusableRSMapperFactory(RSMapper instance) {
        this.instance = instance;
    }

    public RSMapper getInstance(Object[] args, ResultSet resultSet) {
        return instance;
    }
}
