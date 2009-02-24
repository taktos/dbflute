/*
 * Copyright 2004-2008 the Seasar Foundation and the Others.
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
package org.seasar.dbflute.helper.jdbc.schemainitializer;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.seasar.dbflute.helper.jdbc.metadata.DfForeignKeyHandler;
import org.seasar.dbflute.helper.jdbc.metadata.DfTableHandler;
import org.seasar.dbflute.helper.jdbc.metadata.info.DfForeignKeyMetaInfo;
import org.seasar.dbflute.helper.jdbc.metadata.info.DfTableMetaInfo;

/**
 * The schema initializer with JDBC.
 * @author jflute
 */
public class DfSchemaInitializerJdbc implements DfSchemaInitializer {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    private static final Log _log = LogFactory.getLog(DfSchemaInitializerJdbc.class);

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected DataSource _dataSource;

    protected String _schema;

    protected boolean _tableNameWithSchema;

    // /= = = = = = = = = = = =
    // Attribute for once more!
    // = = = = = = = = = =/
    protected List<String> _objectTypeList;

    protected List<String> _tableTargetList;

    protected List<String> _tableExceptList;

    protected boolean _dropAllTable;

    // ===================================================================================
    //                                                                   Initialize Schema
    //                                                                   =================
    public void initializeSchema() {
        Connection conn = null;
        try {
            conn = _dataSource.getConnection();
            final List<DfTableMetaInfo> tableMetaInfoList;
            try {
                final DatabaseMetaData dbMetaData = conn.getMetaData();
                final DfTableHandler tableNameHandler = new DfTableHandler() {
                    // /= = = = = = = = = = = =
                    // Override for once more!
                    // = = = = = = = = = =/
                    @Override
                    protected String[] getObjectTypeStringArray() {
                        if (_objectTypeList != null && !_objectTypeList.isEmpty()) {
                            return _objectTypeList.toArray(new String[] {});
                        } else {
                            return super.getObjectTypeStringArray();
                        }
                    }

                    @Override
                    protected List<String> getTableTargetList() {
                        if (_dropAllTable) {
                            return new ArrayList<String>();
                        }
                        if (_tableTargetList != null && !_tableTargetList.isEmpty()) {
                            return _tableTargetList;
                        } else {
                            return super.getTableTargetList();
                        }
                    }

                    @Override
                    protected List<String> getTableExceptList() {
                        if (_dropAllTable) {
                            return new ArrayList<String>();
                        }
                        if (_tableExceptList != null && !_tableExceptList.isEmpty()) {
                            return _tableExceptList;
                        } else {
                            return super.getTableExceptList();
                        }
                    }
                };
                tableMetaInfoList = tableNameHandler.getTableList(dbMetaData, _schema);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            truncateTableIfPossible(conn, tableMetaInfoList);
            dropForeignKey(conn, tableMetaInfoList);
            dropTable(conn, tableMetaInfoList);
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException ignored) {
                    _log.info("connection.close() threw the exception!", ignored);
                }
            }
        }
    }

    // ===================================================================================
    //                                                                      Truncate Table
    //                                                                      ==============
    protected void truncateTableIfPossible(Connection connection, List<DfTableMetaInfo> tableMetaInfoList) {
        final DfTruncateTableByJdbcCallback callback = new DfTruncateTableByJdbcCallback() {
            public String buildTruncateTableSql(DfTableMetaInfo metaInfo) {
                final StringBuilder sb = new StringBuilder();
                sb.append("truncate table ").append(filterTableName(metaInfo.getTableName()));
                return sb.toString();
            }
        };
        callbackTruncateTableByJdbc(connection, tableMetaInfoList, callback);
    }

    protected static interface DfTruncateTableByJdbcCallback {
        public String buildTruncateTableSql(DfTableMetaInfo metaInfo);
    }

    protected void callbackTruncateTableByJdbc(Connection connection, List<DfTableMetaInfo> tableMetaInfoList,
            DfTruncateTableByJdbcCallback callback) {
        for (DfTableMetaInfo metaInfo : tableMetaInfoList) {
            final String truncateTableSql = callback.buildTruncateTableSql(metaInfo);
            Statement statement = null;
            try {
                statement = connection.createStatement();
                statement.execute(truncateTableSql);
                _log.info(truncateTableSql);
            } catch (Exception e) {
                continue;
            } finally {
                if (statement != null) {
                    try {
                        statement.close();
                    } catch (SQLException ignored) {
                        _log.info("statement.close() threw the exception!", ignored);
                    }
                }
            }
        }
    }

    // ===================================================================================
    //                                                                    Drop Foreign Key
    //                                                                    ================
    protected void dropForeignKey(Connection connection, List<DfTableMetaInfo> tableMetaInfoList) {
        final DfDropForeignKeyByJdbcCallback callback = new DfDropForeignKeyByJdbcCallback() {
            public String buildDropForeignKeySql(DfForeignKeyMetaInfo metaInfo) {
                final String foreignKeyName = metaInfo.getForeignKeyName();
                final String localTableName = filterTableName(metaInfo.getLocalTableName());
                final StringBuilder sb = new StringBuilder();
                sb.append("alter table ").append(localTableName).append(" drop constraint ").append(foreignKeyName);
                return sb.toString();
            }
        };
        callbackDropForeignKeyByJdbc(connection, tableMetaInfoList, callback);
    }

    protected static interface DfDropForeignKeyByJdbcCallback {
        public String buildDropForeignKeySql(DfForeignKeyMetaInfo metaInfo);
    }

    protected void callbackDropForeignKeyByJdbc(Connection connection, List<DfTableMetaInfo> tableMetaInfoList,
            DfDropForeignKeyByJdbcCallback callback) {
        Statement statement = null;
        try {
            statement = connection.createStatement();
            for (DfTableMetaInfo tableMetaInfo : tableMetaInfoList) {
                if (isSkipDropForeignKey(tableMetaInfo)) {
                    continue;
                }
                final DfForeignKeyHandler handler = new DfForeignKeyHandler();
                final DatabaseMetaData dbMetaData = connection.getMetaData();
                final Map<String, DfForeignKeyMetaInfo> foreignKeyMetaInfoMap = handler.getForeignKeyMetaInfo(
                        dbMetaData, _schema, tableMetaInfo);
                final Set<String> keySet = foreignKeyMetaInfoMap.keySet();
                for (String foreignKeyName : keySet) {
                    final DfForeignKeyMetaInfo foreignKeyMetaInfo = foreignKeyMetaInfoMap.get(foreignKeyName);
                    final String dropForeignKeySql = callback.buildDropForeignKeySql(foreignKeyMetaInfo);
                    _log.info(dropForeignKeySql);
                    statement.execute(dropForeignKeySql);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException ignored) {
                    _log.info("statement.close() threw the exception!", ignored);
                }
            }
        }
    }

    protected boolean isSkipDropForeignKey(DfTableMetaInfo tableMetaInfo) {// for sub class.
        return false;
    }

    // ===================================================================================
    //                                                                          Drop Table
    //                                                                          ==========
    protected void dropTable(Connection connection, List<DfTableMetaInfo> tableMetaInfoList) {
        List<DfTableMetaInfo> viewList = new ArrayList<DfTableMetaInfo>();
        List<DfTableMetaInfo> otherList = new ArrayList<DfTableMetaInfo>();
        for (DfTableMetaInfo tableMetaInfo : tableMetaInfoList) {
            if (tableMetaInfo.isTableTypeView()) {
                viewList.add(tableMetaInfo);
            } else {
                otherList.add(tableMetaInfo);
            }
        }

        // Drop view and drop others
        final List<DfTableMetaInfo> sortedList = new ArrayList<DfTableMetaInfo>();
        sortedList.addAll(viewList);
        sortedList.addAll(otherList);

        final DfDropTableByJdbcCallback callback = new DfDropTableByJdbcCallback() {
            public String buildDropTableSql(DfTableMetaInfo metaInfo) {
                final StringBuilder sb = new StringBuilder();
                setupDropTable(sb, metaInfo);
                return sb.toString();
            }

            public String buildDropMaterializedViewSql(DfTableMetaInfo metaInfo) {
                final StringBuilder sb = new StringBuilder();
                sb.append("drop materialized view ").append(metaInfo.getTableName());
                return sb.toString();
            }
        };
        callbackDropTableByJdbc(connection, sortedList, callback);
    }

    protected void setupDropTable(StringBuilder sb, DfTableMetaInfo metaInfo) {
        final String tableName = filterTableName(metaInfo.getTableName());
        if (metaInfo.isTableTypeView()) {
            sb.append("drop view ").append(tableName);
        } else {
            sb.append("drop table ").append(tableName);
        }
    }

    protected static interface DfDropTableByJdbcCallback {
        public String buildDropTableSql(DfTableMetaInfo metaInfo);

        public String buildDropMaterializedViewSql(DfTableMetaInfo metaInfo);
    }

    protected void callbackDropTableByJdbc(Connection connection, List<DfTableMetaInfo> tableMetaInfoList,
            DfDropTableByJdbcCallback callback) {
        Statement statement = null;
        try {
            statement = connection.createStatement();
            for (DfTableMetaInfo metaInfo : tableMetaInfoList) {
                final String dropTableSql = callback.buildDropTableSql(metaInfo);
                _log.info(dropTableSql);
                try {
                    statement.execute(dropTableSql);
                } catch (SQLException e) {
                    // = = = = = = = = = = = =
                    // for materialized view!
                    // = = = = = = = = = = = =
                    final String dropMaterializedViewSql = callback.buildDropMaterializedViewSql(metaInfo);
                    try {
                        statement.execute(dropMaterializedViewSql);
                        _log.info("  --> " + dropMaterializedViewSql);
                    } catch (SQLException ignored) {
                        if (metaInfo.isTableTypeView()) {
                            _log.info("The drop view failed to execute: msg=" + e.getMessage());
                        } else {
                            throw e;
                        }
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException ignored) {
                    _log.info("statement.close() threw the exception!", ignored);
                }
            }
        }
    }

    // ===================================================================================
    //                                                                       Assist Helper
    //                                                                       =============
    protected String filterTableName(String tableName) {
        if (_tableNameWithSchema) {
            tableName = _schema + "." + tableName;
        }
        return tableName;
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public void setDataSource(DataSource dataSource) {
        _dataSource = dataSource;
    }

    public void setSchema(String schema) {
        _schema = schema;
    }

    public List<String> getObjectTypeList() {
        return _objectTypeList;
    }

    public void setObjectTypeList(List<String> objectTypeList) {
        this._objectTypeList = objectTypeList;
    }

    public boolean isTableNameWithSchema() {
        return _tableNameWithSchema;
    }

    public void setTableNameWithSchema(boolean tableNameWithSchema) {
        this._tableNameWithSchema = tableNameWithSchema;
    }

    public List<String> getTableTargetList() {
        return _tableTargetList;
    }

    public void setTableTargetList(List<String> tableTargetList) {
        _tableTargetList = tableTargetList;
    }

    public List<String> getTableExceptList() {
        return _tableExceptList;
    }

    public void setTableExceptList(List<String> tableExceptList) {
        _tableExceptList = tableExceptList;
    }

    public boolean isDropAllTable() {
        return _dropAllTable;
    }

    public void setDropAllTable(boolean dropAllTable) {
        _dropAllTable = dropAllTable;
    }
}