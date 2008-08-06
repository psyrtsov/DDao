/**
 * Copyright 2008 Pavel Syrtsov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations
 * under the License.
 */

package com.syrtsov.ddao.factory;

import com.syrtsov.ddao.factory.param.StatementParameter;
import com.syrtsov.ddao.factory.param.StatementParameterException;
import com.syrtsov.ddao.factory.param.StatementParameterManager;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

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
public class DefaultStatementFactory implements StatementFactory {
    public static final Logger log = Logger.getLogger(DefaultStatementFactory.class.getName());
    private List<StatementParameter> inlineParametersList = new ArrayList<StatementParameter>();
    private List<StatementParameter> refParametersList = new ArrayList<StatementParameter>();
    private List<String> stmtTokens = new ArrayList<String>();

    public void init(String sql, Method method) throws StatementFactoryException {
        try {
            char lastChar = 0;
            boolean paramStarted = false;
            StringBuilder token = new StringBuilder(sql.length());
            StringBuilder param = new StringBuilder();
            for (char ch : sql.toCharArray()) {
                if (paramStarted) {
                    if (lastChar == ch) {
                        if (ch == '$') {
                            stmtTokens.add(token.toString());
                            token.delete(0, token.length());
                            addInlineParameter(method, param.toString());
                        } else {
                            token.append('?');
                            addRefParameter(method, param.toString());
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
            throw new StatementFactoryException("Failed to extract statement parameters for " + method, e);
        }
    }

    public void addRefParameter(Method method, String name) throws StatementParameterException {
        StatementParameter parameter = StatementParameterManager.createStatementParameter(method, name);
        refParametersList.add(parameter);
    }

    public void addInlineParameter(Method method, String name) throws StatementParameterException {
        StatementParameter parameter = StatementParameterManager.createStatementParameter(method, name);
        inlineParametersList.add(parameter);
    }

    public PreparedStatement createStatement(Connection connection, Object[] args) throws StatementFactoryException {
        Iterator<String> iterator = stmtTokens.iterator();
        String stmt = iterator.next();
        try {
            if (iterator.hasNext()) {
                StringBuilder sb = new StringBuilder();
                sb.append(stmt);
                for (StatementParameter statementParameter : inlineParametersList) {
                    String p = statementParameter.extractParam(args);
                    sb.append(p);
                    sb.append(iterator.next());
                }
                stmt = sb.toString();
            }
            log.fine(stmt);
            PreparedStatement preparedStatement = connection.prepareStatement(stmt);
            int i = 1;
            for (StatementParameter statementParameter : refParametersList) {
                statementParameter.bind(preparedStatement, i++, args);
            }
            return preparedStatement;
        } catch (Exception e) {
            throw new StatementFactoryException("Failed to prepare statement '" + stmt + "'", e);
        }
    }
}
