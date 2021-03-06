/*
 * Copyright 2008 Pavel Syrtsov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and limitations
 *  under the License.
 */

package com.sf.ddao.factory;

import com.sf.ddao.conn.ConnectionHandlerHelper;
import com.sf.ddao.factory.param.ParameterException;
import com.sf.ddao.factory.param.ParameterFactory;
import com.sf.ddao.factory.param.ParameterHandler;
import org.apache.commons.chain.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.lang.reflect.AnnotatedElement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * DefaultStatementFactory creates new prepared statment using simplified IBATIS like query syntax: <br/>
 * value reference can be put using number that reflect 0 based method argument number
 * that will be used as simle value
 * (and as such can not be java bean or any other non trivial object)
 * or it can be name of java bean property or Map object that is 1st argument in the
 * list of method arguments.
 * Query paramter can be added as inline value by enclosing it in '$' or
 * it can be added as bound value by enclosing it in '#'. Inline value will be injected in
 * query text before query will be passed to JDBC connection. Bound value will be bound to
 * prepared statement before it's execution.
 * <p/>
 * Created by Pavel Syrtsov
 * Date: Nov 3, 2007
 * Time: 9:24:44 PM
 */
@SuppressWarnings("UnusedDeclaration")
public class DefaultStatementFactory implements StatementFactory {
    public static final Logger log = LoggerFactory.getLogger(DefaultStatementFactory.class.getName());
    private List<ParameterHandler> inlineParametersList = new ArrayList<ParameterHandler>();
    private List<ParameterHandler> refParametersList = new ArrayList<ParameterHandler>();
    private List<String> stmtTokens = new ArrayList<String>();
    @Inject
    private ParameterFactory parameterFactory;

    public void init(AnnotatedElement element, String sql) throws StatementFactoryException {
        try {
            char lastChar = 0;
            boolean paramStarted = false;
            StringBuilder token = new StringBuilder(sql.length());
            StringBuilder param = new StringBuilder();
            for (char ch : sql.toCharArray()) {
                if (paramStarted) {
                    if (lastChar == ch) {
                        stmtTokens.add(token.toString());
                        token.delete(0, token.length());
                        if (ch == '$') {
                            addInlineParameter(element, param.toString());
                        } else {
                            addRefParameter(element, param.toString());
                        }
                        param.delete(0, param.length());
                        paramStarted = false;
                        continue;
                    }
                    param.append(ch);
                    continue;
                }
                if (ch == '#' || ch == '$') {
                    paramStarted = true;
                    lastChar = ch;
                    continue;
                }
                token.append(ch);
            }
            stmtTokens.add(token.toString());
        } catch (Exception e) {
            throw new StatementFactoryException("Failed to extract statement parameters for " + element, e);
        }
    }

    public void addRefParameter(AnnotatedElement element, String name) throws ParameterException {
        ParameterHandler parameter = parameterFactory.createStatementParameter(element, name, true);
        refParametersList.add(parameter);
        inlineParametersList.add(parameter);
    }

    public void addInlineParameter(AnnotatedElement element, String name) throws ParameterException {
        ParameterHandler parameter = parameterFactory.createStatementParameter(element, name, false);
        inlineParametersList.add(parameter);
    }

    public PreparedStatement createStatement(Context context, boolean returnGeneratedKeys) throws StatementFactoryException {
        String stmt = null;
        try {
            stmt = createText(context);
            log.debug("Created statement:{}, applying parameters: {}", stmt, refParametersList);
            final Connection connection = ConnectionHandlerHelper.getConnection(context);
            PreparedStatement preparedStatement;
            if (returnGeneratedKeys) {
                preparedStatement = connection.prepareStatement(stmt, Statement.RETURN_GENERATED_KEYS);
            } else {
                preparedStatement = connection.prepareStatement(stmt);
            }
            int i = 1;
            for (ParameterHandler parameter : refParametersList) {
                i += parameter.bindParam(preparedStatement, i, context);
            }
            return preparedStatement;
        } catch (Exception e) {
            if (stmt == null) {
                stmt = stmtTokens.toString();
            }
            throw new StatementFactoryException("Failed to prepare statement '" + stmt + "'", e);
        }
    }

    public String createText(Context context) throws SQLException {
        Iterator<String> iterator = stmtTokens.iterator();
        String stmt = iterator.next();
        if (iterator.hasNext()) {
            StringBuilder sb = new StringBuilder();
            sb.append(stmt);
            for (ParameterHandler parameter : inlineParametersList) {
                parameter.appendParam(context, sb);
                sb.append(iterator.next());
            }
            stmt = sb.toString();
        }
        return stmt;
    }

    public List<ParameterHandler> getInlineParametersList() {
        return inlineParametersList;
    }

    public List<ParameterHandler> getRefParametersList() {
        return refParametersList;
    }

    public List<String> getStmtTokens() {
        return stmtTokens;
    }

    public ParameterFactory getParameterFactory() {
        return parameterFactory;
    }
}
