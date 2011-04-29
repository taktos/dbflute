package org.seasar.dbflute.logic.replaceschema.allmain;

import java.io.File;
import java.io.FilenameFilter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.torque.engine.database.model.UnifiedSchema;
import org.seasar.dbflute.helper.StringKeyMap;
import org.seasar.dbflute.helper.StringSet;
import org.seasar.dbflute.helper.jdbc.DfRunnerInformation;
import org.seasar.dbflute.helper.jdbc.sqlfile.DfSqlFileFireMan;
import org.seasar.dbflute.helper.jdbc.sqlfile.DfSqlFileFireResult;
import org.seasar.dbflute.helper.jdbc.sqlfile.DfSqlFileRunner;
import org.seasar.dbflute.helper.jdbc.sqlfile.DfSqlFileRunnerDispatcher;
import org.seasar.dbflute.helper.jdbc.sqlfile.DfSqlFileRunnerExecute;
import org.seasar.dbflute.logic.replaceschema.schemainitializer.DfSchemaInitializer;
import org.seasar.dbflute.logic.replaceschema.schemainitializer.factory.DfSchemaInitializerFactory;
import org.seasar.dbflute.logic.replaceschema.schemainitializer.factory.DfSchemaInitializerFactory.InitializeType;
import org.seasar.dbflute.properties.DfDatabaseProperties;
import org.seasar.dbflute.properties.DfReplaceSchemaProperties;
import org.seasar.dbflute.util.DfCollectionUtil;
import org.seasar.dbflute.util.DfStringUtil;
import org.seasar.dbflute.util.Srl;

public class DfCreateSchemaMain extends DfAbstractReplaceSchemaMain {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    /** Log instance. */
    private static final Log _log = LogFactory.getLog(DfCreateSchemaMain.class);

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final CreatingDataSourceHandler _dataSourceHandler;

    protected boolean _lazyConnection;
    protected String _currentUser;
    protected StringSet _goodByeUserSet = StringSet.createAsCaseInsensitive();
    protected StringSet _revivedUserSet = StringSet.createAsCaseInsensitive();
    protected StringKeyMap<Connection> _changeUserConnectionMap = StringKeyMap.createAsCaseInsensitive();

    // ===================================================================================
    //                                                                             Execute
    //                                                                             =======
    public DfCreateSchemaMain(CreatingDataSourceHandler dataSourceHandler, boolean lazyConnection) {
        _dataSourceHandler = dataSourceHandler;
        _lazyConnection = lazyConnection;
    }

    public static interface CreatingDataSourceHandler {
        DataSource callbackGetDataSource();

        void callbackSetupDataSource() throws SQLException;
    }

    protected DataSource getDataSource() {
        return _dataSourceHandler.callbackGetDataSource();
    }

    protected void setupDataSource() throws SQLException {
        _dataSourceHandler.callbackSetupDataSource();
    }

    // ===================================================================================
    //                                                                             Execute
    //                                                                             =======
    public void execute() {
        _log.info("");
        _log.info("{ReplaceSchema Properties}");
        _log.info("errorContinue     = " + getReplaceSchemaProperties().isErrorContinue());
        _log.info("sqlFileEncoding   = " + getReplaceSchemaProperties().getSqlFileEncoding());
        initializeSchema();

        final DfRunnerInformation runInfo = createRunnerInformation();
        createSchema(runInfo);
    }

    // ===================================================================================
    //                                                                   Initialize Schema
    //                                                                   =================
    protected void initializeSchema() {
        // additional first for dropping references to main schema
        initializeSchemaAdditionalDrop();
        initializeSchemaMainDrop();
    }

    protected void initializeSchemaAdditionalDrop() {
        List<Map<String, Object>> additionalDropMapList = getReplaceSchemaProperties().getAdditionalDropMapList();
        if (additionalDropMapList.isEmpty()) {
            return;
        }
        // /= = = = = = = = = = = = = = = = = 
        // Unsupported at MySQL and SQLServer
        // = = = = = = = = = =/
        if (getDatabaseTypeFacadeProp().isDatabaseMySQL() || getDatabaseTypeFacadeProp().isDatabaseSQLServer()) {
            String msg = "AdditionalDropDefinitionSchema is unsupported at MySQL and SQLServer!";
            throw new UnsupportedOperationException(msg);
        }
        _log.info("");
        _log.info("* * * * * * * * * * * * * * * * * * * *");
        _log.info("*                                     *");
        _log.info("* Initialize Schema (Additional Drop) *");
        _log.info("*                                     *");
        _log.info("* * * * * * * * * * * * * * * * * * * *");
        if (_lazyConnection) {
            _log.info("*Passed because it's a lazy connection");
            return;
        }
        for (Map<String, Object> additionalDropMap : additionalDropMapList) {
            final UnifiedSchema dropSchema = getReplaceSchemaProperties().getAdditionalDropSchema(additionalDropMap);
            final String dropUrl = getReplaceSchemaProperties().getAdditionalDropUrl(additionalDropMap);
            final StringBuilder logSb = new StringBuilder();
            if (dropSchema.hasSchema()) {
                logSb.append("[").append(dropSchema.getLoggingSchema()).append("]");
                if (dropUrl != null && dropUrl.trim().length() > 0) {
                    logSb.append(": ").append(dropUrl);
                }
            } else {
                if (dropUrl != null && dropUrl.trim().length() > 0) {
                    logSb.append(dropUrl);
                }
            }
            _log.info(logSb.toString());
            final DfSchemaInitializer initializer = createSchemaInitializerAdditional(additionalDropMap);
            if (initializer != null) {
                initializer.initializeSchema();
            }
        }
    }

    protected void initializeSchemaMainDrop() {
        _log.info("");
        _log.info("* * * * * * * * * * *");
        _log.info("*                   *");
        _log.info("* Initialize Schema *");
        _log.info("*                   *");
        _log.info("* * * * * * * * * * *");
        if (_lazyConnection) {
            _log.info("*Passed because it's a lazy connection");
            return;
        }
        final DfSchemaInitializer initializer = createSchemaInitializer(InitializeType.MAIN);
        if (initializer != null) {
            initializer.initializeSchema();
        }
    }

    protected DfSchemaInitializer createSchemaInitializer(InitializeType initializeType) {
        final DfSchemaInitializerFactory factory = createSchemaInitializerFactory(initializeType);
        return factory.createSchemaInitializer();
    }

    protected DfSchemaInitializer createSchemaInitializerAdditional(Map<String, Object> additionalDropMap) {
        final DfSchemaInitializerFactory factory = createSchemaInitializerFactory(InitializeType.ADDTIONAL);
        factory.setAdditionalDropMap(additionalDropMap);
        return factory.createSchemaInitializer();
    }

    protected DfSchemaInitializerFactory createSchemaInitializerFactory(InitializeType initializeType) {
        return new DfSchemaInitializerFactory(getDataSource(), getDatabaseTypeFacadeProp(), getDatabaseProperties(),
                getReplaceSchemaProperties(), initializeType);
    }

    // ===================================================================================
    //                                                                       Create Schema
    //                                                                       =============
    protected DfRunnerInformation createRunnerInformation() {
        final DfRunnerInformation runInfo = new DfRunnerInformation();
        final DfDatabaseProperties prop = getDatabaseProperties();
        runInfo.setDriver(prop.getDatabaseDriver());
        runInfo.setUrl(prop.getDatabaseUrl());
        runInfo.setUser(prop.getDatabaseUser());
        runInfo.setPassword(prop.getDatabasePassword());
        runInfo.setEncoding(getReplaceSchemaSqlFileEncoding());
        runInfo.setAutoCommit(true);
        runInfo.setErrorContinue(getReplaceSchemaProperties().isErrorContinue());
        runInfo.setRollbackOnly(false);
        return runInfo;
    }

    protected String getReplaceSchemaSqlFileEncoding() {
        return getReplaceSchemaProperties().getSqlFileEncoding();
    }

    protected void createSchema(DfRunnerInformation runInfo) {
        _log.info("");
        _log.info("* * * * * * * * *");
        _log.info("*               *");
        _log.info("* Create Schema *");
        _log.info("*               *");
        _log.info("* * * * * * * * *");
        final DfSqlFileFireMan fireMan = new DfSqlFileFireMan();
        fireMan.setExecutorName("Create Schema");
        final DfSqlFileFireResult result = fireMan.fire(getSqlFileRunner(runInfo), getReplaceSchemaSqlFileList());
        try {
            dumpResult(result);
        } catch (Throwable ignored) {
            _log.warn("*Failed to dump create-schema result: " + result, ignored);
        }
        _log.info(""); // for space line
        destroyChangeUserConnection();
    }

    protected DfSqlFileRunner getSqlFileRunner(final DfRunnerInformation runInfo) {
        final DfReplaceSchemaProperties prop = getReplaceSchemaProperties();
        final DfSqlFileRunnerExecute execute = new DfSqlFileRunnerExecuteCreateSchema(runInfo, getDataSource());
        execute.setDispatcher(new DfSqlFileRunnerDispatcher() {
            public boolean dispatch(File sqlFile, Statement st, String sql) throws SQLException {
                if (_currentUser == null || _currentUser.trim().length() == 0) {
                    return false;
                }
                Connection conn = _changeUserConnectionMap.get(_currentUser);
                if (conn == null) {
                    _log.info("...Creating a connection to " + _currentUser);
                    conn = prop.createAdditionalUserConnection(_currentUser);
                    if (conn != null) {
                        _changeUserConnectionMap.put(_currentUser, conn);
                    }
                    if (conn == null) {
                        String msg = "...Saying good-bye to the user '" + _currentUser + "'";
                        msg = msg + " because of no definition";
                        _log.info(msg);
                        _goodByeUserSet.add(_currentUser);
                        return true;
                    }
                }
                Statement dispatchStmt = null;
                try {
                    dispatchStmt = conn.createStatement();
                } catch (SQLException e) {
                    throw e;
                }
                try {
                    dispatchStmt.execute(sql);
                    return true;
                } catch (SQLException e) {
                    final boolean checkUser = analyzeCheckUser(sql);
                    if (checkUser) {
                        String msg = "...Saying good-bye to the user '" + _currentUser + "'";
                        msg = msg + " because of checked";
                        _log.info(msg);
                        _goodByeUserSet.add(_currentUser);
                        return true;
                    }
                    throw e;
                } finally {
                    if (dispatchStmt != null) {
                        dispatchStmt.close();
                    }
                }
            }
        });
        return execute;
    }

    protected class DfSqlFileRunnerExecuteCreateSchema extends DfSqlFileRunnerExecute {
        public DfSqlFileRunnerExecuteCreateSchema(DfRunnerInformation runInfo, DataSource dataSource) {
            super(runInfo, dataSource);
        }

        @Override
        public void prepare(File sqlFile) {
            super.prepare(sqlFile);
            restoreRevivedUser();
            restoreCurrentUser();
        }

        protected void restoreRevivedUser() {
            for (String revivedUser : _revivedUserSet) {
                if (_goodByeUserSet.contains(revivedUser)) {
                    continue; // already good-bye again
                }
                _log.info("...Saying good-bye to the user '" + revivedUser + "' again");
                _goodByeUserSet.add(revivedUser);
            }
            if (!_revivedUserSet.isEmpty()) {
                _log.info("...Clearing revived users");
                _revivedUserSet.clear();
            }
        }

        protected void restoreCurrentUser() {
            if (_currentUser != null) {
                _log.info("...Coming back to the main user from the user '" + _currentUser + "'");
                _currentUser = null; // because the max scope of change user is one SQL file
            }
        }

        @Override
        protected String filterSql(String sql) {
            sql = super.filterSql(sql);
            sql = getReplaceSchemaProperties().resolveFilterVariablesIfNeeds(sql);
            return sql;
        }

        @Override
        protected boolean isSqlTrimAndRemoveLineSeparator() {
            return true;
        }

        @Override
        protected boolean isHandlingCommentOnLineSeparator() {
            return true;
        }

        @Override
        protected boolean isDbCommentLine(String line) {
            final boolean commentLine = super.isDbCommentLine(line);
            if (commentLine) {
                return commentLine;
            }
            // for irregular pattern
            return isDbCommentLineForIrregularPattern(line);
        }

        @Override
        protected String getTerminater4Tool() {
            return resolveTerminater4Tool();
        }

        @Override
        protected boolean isTargetSql(String sql) {
            final String changeUesr = analyzeChangeUser(sql);
            if (changeUesr != null) {
                _currentUser = changeUesr;
            }
            final boolean backToMainUser = analyzeBackToMainUser(sql);
            if (backToMainUser) {
                _log.info("...Coming back to the main user from the user '" + _currentUser + "'");
                _currentUser = null;
            }
            final boolean reviveUser = analyzeReviveUser(sql);
            if (_currentUser != null && _currentUser.trim().length() > 0) {
                if (_goodByeUserSet.contains(_currentUser)) {
                    if (reviveUser) {
                        _log.info("...Reviving the user '" + _currentUser + "' until the end of this SQL file");
                        _revivedUserSet.add(_currentUser);
                        _goodByeUserSet.remove(_currentUser);
                    } else {
                        String logSql = sql;
                        if (logSql.length() > 30) {
                            logSql = logSql.substring(0, 27) + "...";
                        }
                        _log.info("passed: " + logSql);
                        return false;
                    }
                }
            } else {
                if (reviveUser) {
                    _log.warn("*The mark 'reviveUser()' is unsupported at the timing!");
                }
            }
            return super.isTargetSql(sql);
        }

        @Override
        protected void lazyConnectIfNeeds() throws SQLException {
            if (_lazyConnection) {
                _log.info("...Connecting by main user lazily");
                setupDataSource();
                _dataSource = getDataSource();
                setupConnection();
                setupStatement();
                _lazyConnection = false;
            }
        }
    }

    protected String analyzeChangeUser(String sql) {
        final String beginMark = "#df:changeUser(";
        final int markIndex = sql.indexOf(beginMark);
        if (markIndex < 0) {
            return null;
        }
        final String rear = sql.substring(markIndex + beginMark.length());
        final int endIndex = rear.indexOf(")");
        if (endIndex < 0) {
            String msg = "The command changeUser should have its end mark ')':";
            msg = msg + " example=[#df:changeUser(system)#], sql=" + sql;
            throw new IllegalStateException(msg);
        }
        return rear.substring(0, endIndex).trim();
    }

    protected boolean analyzeCheckUser(String sql) {
        final String mark = "#df:checkUser()#";
        return sql.contains(mark);
    }

    protected boolean analyzeBackToMainUser(String sql) {
        final String mark = "#df:backToMainUser()#";
        return sql.contains(mark);
    }

    protected boolean analyzeReviveUser(String sql) {
        final String mark = "#df:reviveUser()#";
        return sql.contains(mark);
    }

    protected List<File> getReplaceSchemaSqlFileList() {
        final List<File> fileList = new ArrayList<File>();
        fileList.addAll(getReplaceSchemaNextSqlFileList());
        fileList.addAll(getReplaceSchemaNextSqlFileListAdditional());
        return fileList;
    }

    protected List<File> getReplaceSchemaNextSqlFileList() {
        final String path = getReplaceSchemaProperties().getReplaceSchemaPlaySqlDirectory();
        return doGetReplaceSchemaSqlFileList(path);
    }

    protected List<File> getReplaceSchemaNextSqlFileListAdditional() {
        final List<File> fileList = new ArrayList<File>();
        final String path = getReplaceSchemaProperties().getApplicationPlaySqlDirectory();
        if (Srl.is_Null_or_TrimmedEmpty(path)) {
            return DfCollectionUtil.emptyList();
        }
        fileList.addAll(doGetReplaceSchemaSqlFileList(path));
        return fileList;
    }

    protected List<File> doGetReplaceSchemaSqlFileList(String directoryPath) {
        final File baseDir = new File(directoryPath);
        final String fileNameWithoutExt = getReplaceSchemaSqlFileNameWithoutExt();
        final String sqlFileExt = getReplaceSchemaSqlFileExt();
        final FilenameFilter filter = new FilenameFilter() {
            public boolean accept(File dir, String name) {
                if (name.startsWith(fileNameWithoutExt) && name.endsWith("." + sqlFileExt)) {
                    return true;
                }
                return false;
            }
        };

        // order by FileName asc
        final Comparator<File> fileNameAscComparator = new Comparator<File>() {
            public int compare(File o1, File o2) {
                return o1.getName().compareTo(o2.getName());
            }
        };
        final TreeSet<File> treeSet = new TreeSet<File>(fileNameAscComparator);

        final String[] targetList = baseDir.list(filter);
        if (targetList == null) {
            return DfCollectionUtil.emptyList();
        }
        for (String targetFileName : targetList) {
            final String targetFilePath = directoryPath + "/" + targetFileName;
            treeSet.add(new File(targetFilePath));
        }
        return new ArrayList<File>(treeSet);
    }

    protected String getReplaceSchemaSqlFileNameWithoutExt() {
        return getReplaceSchemaProperties().getReplaceSchemaSqlFileNameWithoutExt();
    }

    protected String getReplaceSchemaSqlFileExt() {
        return getReplaceSchemaProperties().getReplaceSchemaSqlFileExt();
    }

    protected void destroyChangeUserConnection() {
        if (_changeUserConnectionMap.isEmpty()) {
            return;
        }
        _log.info("...Closing connections to change-users: " + _changeUserConnectionMap.keySet());
        final Set<Entry<String, Connection>> entrySet = _changeUserConnectionMap.entrySet();
        for (Entry<String, Connection> entry : entrySet) {
            final String changeUser = entry.getKey();
            final Connection conn = entry.getValue();
            try {
                conn.close();
            } catch (SQLException continued) {
                String msg = "Failed to close the connection for " + changeUser + ":";
                msg = msg + " message=" + continued.getMessage();
                _log.info(msg);
            }
        }
    }

    // ===================================================================================
    //                                                                         Result Dump
    //                                                                         ===========
    protected void dumpResult(DfSqlFileFireResult result) {
        final String resultMessage = result.getResultMessage();
        final boolean failure = result.existsError();
        final String detailMessage = result.getDetailMessage();
        final File dumpFile = new File(CREATE_SCHEMA_LOG_PATH);
        dumpProcessResult(dumpFile, resultMessage, failure, detailMessage);
    }

    // ===================================================================================
    //                                                                      General Helper
    //                                                                      ==============
    protected String replaceString(String text, String fromText, String toText) {
        return DfStringUtil.replace(text, fromText, toText);
    }
}