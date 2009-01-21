/*
 * Copyright 2004-2009 the Seasar Foundation and the Others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.seasar.dbflute.s2dao.valuetype.plugin;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;

import org.seasar.dbflute.s2dao.valuetype.registered.SqlDateType;
import org.seasar.dbflute.util.DfTypeUtil;

/**
 * {Refers to Seasar and Extends its class}
 * @author jflute
 */
public class DateSqlDateType extends SqlDateType {

    public Object getValue(ResultSet resultSet, int index) throws SQLException {
        return toDate(super.getValue(resultSet, index));
    }

    public Object getValue(ResultSet resultSet, String columnName) throws SQLException {
        return toDate(super.getValue(resultSet, columnName));
    }

    public Object getValue(CallableStatement cs, int index) throws SQLException {
        return toDate(super.getValue(cs, index));
    }

    public Object getValue(CallableStatement cs, String parameterName) throws SQLException {
        return toDate(super.getValue(cs, parameterName));
    }

    protected Date toDate(Object value) {
        return DfTypeUtil.toDate(value);
    }

    protected java.sql.Date toSqlDate(Object value) {
        Calendar base = Calendar.getInstance();
        base.setTime(toDate(value));
        base.set(Calendar.HOUR_OF_DAY, 0);
        base.set(Calendar.MINUTE, 0);
        base.set(Calendar.SECOND, 0);
        base.set(Calendar.MILLISECOND, 0);
        return new java.sql.Date(base.getTimeInMillis());
    }

}
