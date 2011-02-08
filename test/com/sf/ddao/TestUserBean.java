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

package com.sf.ddao;

import com.sf.ddao.crud.TableName;
import com.sf.ddao.factory.BoundParameter;
import org.apache.commons.chain.Context;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Created-By: Pavel Syrtsov
 * Date: Apr 10, 2008
 * Time: 10:24:20 PM
 */
@TableName("test_user")
public class TestUserBean {
    private long id;
    private String name;
    private String longName;
    private Gender gender = Gender.GIRL;

    @SuppressWarnings({"UnusedDeclaration"})
    private TestUserBean() {
    }

    @SuppressWarnings({"UnusedDeclaration"})
    public TestUserBean(boolean dummy) {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLongName() {
        return longName;
    }

    public void setLongName(String longName) {
        this.longName = longName;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public static enum Gender implements BoundParameter {
        BOY, GIRL;

        public int bindParam(PreparedStatement preparedStatement, int idx, Context context) throws SQLException {
            preparedStatement.setString(idx, name());
            return 1;
        }
    }
}
