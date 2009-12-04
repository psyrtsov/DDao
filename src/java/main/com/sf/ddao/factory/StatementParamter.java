package com.sf.ddao.factory;

import org.apache.commons.chain.Context;

import java.sql.PreparedStatement;

/**
 *
 */
public interface StatementParamter {
    void bind(PreparedStatement preparedStatement, int idx, Context context) throws Exception;

    String statementParameter();
}
