package com.sf.ddao.orm;

import java.sql.ResultSet;

/**
 * Created by psyrtsov
 */
public interface RSMapperFactory {
    RSMapper getInstance(Object[] args, ResultSet resultSet);
}
