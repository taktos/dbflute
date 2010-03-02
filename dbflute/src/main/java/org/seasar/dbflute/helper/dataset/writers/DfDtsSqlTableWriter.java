package org.seasar.dbflute.helper.dataset.writers;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.seasar.dbflute.helper.dataset.DfDataRow;
import org.seasar.dbflute.helper.dataset.DfDataTable;
import org.seasar.dbflute.helper.dataset.states.DfDtsRowState;

/**
 * {Refers to S2Container and Extends it}
 * @author jflute
 * @since 0.8.3 (2008/10/28 Tuesday)
 */
public class DfDtsSqlTableWriter implements DfDtsTableWriter {

    private DataSource dataSource;
    protected String _schemaName;

    public DfDtsSqlTableWriter(DataSource dataSource, String schemaName) {
        this.dataSource = dataSource;
        _schemaName = schemaName;
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public void write(DfDataTable table) {
        if (!table.hasMetaData()) {
            setupMetaData(table);
        }
        doWrite(table);
    }

    protected void doWrite(DfDataTable table) {
        for (int i = 0; i < table.getRowSize(); ++i) {
            DfDataRow row = table.getRow(i);
            DfDtsRowState state = row.getState();
            state.update(dataSource, row);
        }
    }

    private void setupMetaData(DfDataTable table) {
        Connection con = getConnection(dataSource);
        try {
            table.setupMetaData(getMetaData(con), _schemaName);
        } finally {
            close(con);
        }
    }

    private static Connection getConnection(DataSource dataSource) {
        try {
            return dataSource.getConnection();
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }

    private static DatabaseMetaData getMetaData(Connection conn) {
        try {
            return conn.getMetaData();
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }

    private static void close(Connection conn) {
        if (conn == null)
            return;
        try {
            conn.close();
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }
}