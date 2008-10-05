package com.syrtsov.ddao;

import com.syrtsov.ddao.conn.TransactionStarter;

import java.sql.Connection;

/**
 * User: Pavel Syrtsov
 * Date: Sep 27, 2008
 * Time: 8:54:19 PM
 * psdo: provide comments for class ${CLASSNAME}
 */
public interface TransactionableDao {
    @TransactionStarter
    Connection startTransaction();
}
