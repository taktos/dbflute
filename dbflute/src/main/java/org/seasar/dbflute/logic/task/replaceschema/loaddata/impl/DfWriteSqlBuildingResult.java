package org.seasar.dbflute.logic.task.replaceschema.loaddata.impl;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author jflute
 */
public class DfWriteSqlBuildingResult {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected String sql;
    protected Map<String, Object> columnValueMap = new LinkedHashMap<String, Object>();

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public Map<String, Object> getColumnValueMap() {
        return columnValueMap;
    }

    public void addColumnValue(String columnName, Object value) {
        this.columnValueMap.put(columnName, value);
    }
}