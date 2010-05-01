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
package org.seasar.dbflute.exception.handler;

import java.sql.SQLException;
import java.sql.Statement;

import org.seasar.dbflute.cbean.ConditionBean;
import org.seasar.dbflute.cbean.ConditionBeanContext;
import org.seasar.dbflute.exception.EntityAlreadyExistsException;
import org.seasar.dbflute.exception.SQLFailureException;
import org.seasar.dbflute.exception.factory.ExceptionMessageBuilder;
import org.seasar.dbflute.outsidesql.OutsideSqlContext;
import org.seasar.dbflute.resource.InternalMapContext;
import org.seasar.dbflute.resource.ResourceContext;
import org.seasar.dbflute.util.DfSystemUtil;

/**
 * @author jflute
 */
public class SQLExceptionHandler {

    // ===================================================================================
    //                                                                              Handle
    //                                                                              ======
    /**
     * @param e The instance of SQLException. (NotNull)
     */
    public void handleSQLException(SQLException e) {
        handleSQLException(e, null, false);
    }

    /**
     * @param e The instance of SQLException. (NotNull)
     * @param st The instance of statement. (Nullable)
     */
    public void handleSQLException(SQLException e, Statement st) {
        handleSQLException(e, st, false);
    }

    public void handleSQLException(SQLException e, Statement st, boolean uniqueConstraintValid) {
        handleSQLException(e, st, uniqueConstraintValid, null);
    }

    public void handleSQLException(SQLException e, Statement st, boolean uniqueConstraintValid, String completeSql) {
        if (uniqueConstraintValid && isUniqueConstraintException(e)) {
            throwEntityAlreadyExistsException(e, st, completeSql);
        }
        throwSQLFailureException(e, st, completeSql);
    }

    protected boolean isUniqueConstraintException(SQLException e) {
        if (!ResourceContext.isExistResourceContextOnThread()) {
            return false;
        }
        return ResourceContext.isUniqueConstraintException(extractSQLState(e), e.getErrorCode());
    }

    // ===================================================================================
    //                                                                               Throw
    //                                                                               =====
    protected void throwEntityAlreadyExistsException(SQLException e, Statement st, String displaySql) {
        final ExceptionMessageBuilder br = createExceptionMessageBuilder();
        br.addNotice("The entity already exists on the database!");
        br.addItem("Advice");
        br.addElement("Please confirm the primary key whether it already exists on the database.");
        br.addElement("And confirm the unique constraint for other columns.");
        setupCommonElement(br, e, st, displaySql);
        final String msg = br.buildExceptionMessage();
        throw new EntityAlreadyExistsException(msg, e);
    }

    protected void throwSQLFailureException(SQLException e, Statement statement, String displaySql) {
        final ExceptionMessageBuilder br = createExceptionMessageBuilder();
        br.addNotice("The SQL failed to execute!");
        br.addItem("Advice");
        br.addElement("Please confirm the SQLException message.");
        setupCommonElement(br, e, statement, displaySql);
        final String msg = br.buildExceptionMessage();
        throw new SQLFailureException(msg, e);
    }

    protected ExceptionMessageBuilder createExceptionMessageBuilder() {
        return new ExceptionMessageBuilder();
    }

    // ===================================================================================
    //                                                                             Element
    //                                                                             =======
    protected void setupCommonElement(ExceptionMessageBuilder br, SQLException e, Statement st, String displaySql) {
        br.addItem("SQLState");
        br.addElement(extractSQLState(e));
        br.addItem("ErrorCode");
        br.addElement(e.getErrorCode());
        setupSQLExceptionElement(br, e);
        setupBehaviorElement(br);
        setupConditionBeanElement(br);
        setupOutsideSqlElement(br);
        setupStatementElement(br, st);
        setupDisplaySqlElement(br, displaySql);
    }

    protected void setupSQLExceptionElement(ExceptionMessageBuilder br, SQLException e) {
        br.addItem("SQLException");
        br.addElement(e.getClass().getName());
        br.addElement(extractMessage(e));
        final SQLException nextEx = e.getNextException();
        if (nextEx != null) {
            br.addItem("NextException");
            br.addElement(nextEx.getClass().getName());
            br.addElement(extractMessage(nextEx));
            final SQLException nextNextEx = nextEx.getNextException();
            if (nextNextEx != null) {
                br.addItem("NextNextException");
                br.addElement(nextNextEx.getClass().getName());
                br.addElement(extractMessage(nextNextEx));
            }
        }
    }

    protected void setupBehaviorElement(ExceptionMessageBuilder br) {
        final Object invokeName = extractBehaviorInvokeName();
        if (invokeName != null) {
            br.addItem("Behavior");
            br.addElement(invokeName);
        }
    }

    protected void setupConditionBeanElement(ExceptionMessageBuilder br) {
        if (hasConditionBean()) {
            br.addItem("ConditionBean"); // only class name because of already existing displaySql
            br.addElement(getConditionBean().getClass().getName());
        }
    }

    protected void setupOutsideSqlElement(ExceptionMessageBuilder br) {
        if (hasOutsideSqlContext()) {
            br.addItem("OutsideSql");
            br.addElement(getOutsideSqlContext().getOutsideSqlPath());
        }
    }

    // *because of existing displaySql instead
    //protected void setupParameterBeanElement(ExceptionMessageBuilder br) {
    //    if (hasOutsideSqlContext()) {
    //        br.addItem("ParameterBean");
    //        br.addElement(getOutsideSqlContext().getParameterBean());
    //    }
    //}

    protected void setupStatementElement(ExceptionMessageBuilder br, Statement st) {
        if (st != null) {
            br.addItem("Statement");
            br.addElement(st.getClass().getName());
        }
    }

    protected void setupDisplaySqlElement(ExceptionMessageBuilder br, String displaySql) {
        if (displaySql != null) {
            br.addItem("Display SQL");
            br.addElement(displaySql);
        }
    }

    // ===================================================================================
    //                                                                             Extract
    //                                                                             =======
    protected String extractMessage(SQLException e) {
        String message = e.getMessage();

        // Because a message of Oracle contains a line separator.
        return message != null ? message.trim() : message;
    }

    protected String extractSQLState(SQLException e) {
        String sqlState = e.getSQLState();
        if (sqlState != null) {
            return sqlState;
        }

        // Next
        SQLException nextEx = e.getNextException();
        if (nextEx == null) {
            return null;
        }
        sqlState = nextEx.getSQLState();
        if (sqlState != null) {
            return sqlState;
        }

        // Next Next
        SQLException nextNextEx = nextEx.getNextException();
        if (nextNextEx == null) {
            return null;
        }
        sqlState = nextNextEx.getSQLState();
        if (sqlState != null) {
            return sqlState;
        }

        // Next Next Next
        SQLException nextNextNextEx = nextNextEx.getNextException();
        if (nextNextNextEx == null) {
            return null;
        }
        sqlState = nextNextNextEx.getSQLState();
        if (sqlState != null) {
            return sqlState;
        }

        // It doesn't use recursive call by design because JDBC is unpredictable fellow.
        return null;
    }

    protected String extractBehaviorInvokeName() {
        final Object behaviorInvokeName = InternalMapContext.getObject("df:BehaviorInvokeName");
        if (behaviorInvokeName == null) {
            return null;
        }
        final Object clientInvokeName = InternalMapContext.getObject("df:ClientInvokeName");
        final Object byPassInvokeName = InternalMapContext.getObject("df:ByPassInvokeName");
        final StringBuilder sb = new StringBuilder();
        boolean existsPath = false;
        if (clientInvokeName != null) {
            existsPath = true;
            sb.append(clientInvokeName);
        }
        if (byPassInvokeName != null) {
            existsPath = true;
            sb.append(byPassInvokeName);
        }
        sb.append(behaviorInvokeName);
        if (existsPath) {
            sb.append("...");
        }
        return sb.toString();
    }

    // ===================================================================================
    //                                                                       Assist Helper
    //                                                                       =============
    protected boolean hasConditionBean() {
        return ConditionBeanContext.isExistConditionBeanOnThread();
    }

    protected ConditionBean getConditionBean() {
        return ConditionBeanContext.getConditionBeanOnThread();
    }

    protected boolean hasOutsideSqlContext() {
        return OutsideSqlContext.isExistOutsideSqlContextOnThread();
    }

    protected OutsideSqlContext getOutsideSqlContext() {
        return OutsideSqlContext.getOutsideSqlContextOnThread();
    }

    protected String ln() {
        return DfSystemUtil.getLineSeparator();
    }
}