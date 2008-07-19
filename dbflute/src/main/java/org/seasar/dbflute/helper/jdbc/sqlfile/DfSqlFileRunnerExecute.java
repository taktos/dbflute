package org.seasar.dbflute.helper.jdbc.sqlfile;

import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.seasar.dbflute.helper.jdbc.DfRunnerInformation;

/**
 * @author jflute
 */
public class DfSqlFileRunnerExecute extends DfSqlFileRunnerBase {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    /** Log instance. */
    private static Log _log = LogFactory.getLog(DfSqlFileRunnerExecute.class);

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public DfSqlFileRunnerExecute(DfRunnerInformation runInfo, DataSource dataSource) {
        super(runInfo, dataSource);
    }

    // ===================================================================================
    //                                                                         Execute SQL
    //                                                                         ===========
    /**
     * Execute the SQL statement.
     * @param statement Statement. (NotNull)
     * @param sql SQL. (NotNull)
     */
    protected void execSQL(Statement statement, String sql) {
        try {
            statement.execute(sql);
            _goodSqlCount++;
        } catch (SQLException e) {
            if (!_runInfo.isErrorContinue()) {
                String msg = "Look! Read the message below." + getLineSeparator();
                msg = msg + "/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *" + getLineSeparator();
                msg = msg + "It failed to execute the SQL!" + getLineSeparator();
                msg = msg + getLineSeparator();
                msg = msg + "[Executed SQL]" + getLineSeparator();
                msg = msg + sql + getLineSeparator();
                msg = msg + getLineSeparator();
                msg = msg + "[SQLException]" + getLineSeparator();
                msg = msg + e.getMessage() + getLineSeparator();
                msg = msg + "* * * * * * * * * */";
                throw new DfSQLExecutionFailureException(msg, e);
            }
            _log.warn("Failed to execute: " + sql, e);
            _log.warn("" + System.getProperty("line.separator"));
        }
    }

    protected String getLineSeparator() {
        return System.getProperty("line.separator");
    }
}
