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
package org.seasar.dbflute.s2dao.valuetype.registered;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import org.seasar.dbflute.s2dao.valuetype.TnAbstractValueType;
import org.seasar.dbflute.util.DfTypeUtil;

/**
 * {Refers to S2Container's utility and Extends it}
 * @author jflute
 */
public class DoubleType extends TnAbstractValueType {

    public DoubleType() {
        super(Types.DOUBLE);
    }

    public Object getValue(ResultSet resultSet, int index) throws SQLException {
        return DfTypeUtil.toDouble(resultSet.getObject(index));
    }

    public Object getValue(ResultSet resultSet, String columnName) throws SQLException {
        return DfTypeUtil.toDouble(resultSet.getObject(columnName));
    }

    public Object getValue(CallableStatement cs, int index) throws SQLException {
        return DfTypeUtil.toDouble(cs.getObject(index));
    }

    public Object getValue(CallableStatement cs, String parameterName) throws SQLException {
        return DfTypeUtil.toDouble(cs.getObject(parameterName));
    }

    public void bindValue(PreparedStatement ps, int index, Object value) throws SQLException {
        if (value == null) {
            setNull(ps, index);
        } else {
            ps.setDouble(index, DfTypeUtil.toPrimitiveDouble(value));
        }
    }

    public void bindValue(CallableStatement cs, String parameterName, Object value) throws SQLException {
        if (value == null) {
            setNull(cs, parameterName);
        } else {
            cs.setDouble(parameterName, DfTypeUtil.toPrimitiveDouble(value));
        }
    }

    public String toText(Object value) {
        if (value == null) {
            return DfTypeUtil.nullText();
        }
        Double var = DfTypeUtil.toDouble(value);
        return DfTypeUtil.toText(var);
    }
}