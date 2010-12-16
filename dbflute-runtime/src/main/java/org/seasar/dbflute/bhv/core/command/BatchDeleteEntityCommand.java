/*
 * Copyright 2004-2010 the Seasar Foundation and the Others.
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
package org.seasar.dbflute.bhv.core.command;

import org.seasar.dbflute.bhv.DeleteOption;
import org.seasar.dbflute.bhv.core.SqlExecution;
import org.seasar.dbflute.bhv.core.SqlExecutionCreator;
import org.seasar.dbflute.cbean.ConditionBean;
import org.seasar.dbflute.dbmeta.DBMeta;
import org.seasar.dbflute.s2dao.metadata.TnBeanMetaData;
import org.seasar.dbflute.s2dao.sqlcommand.TnBatchDeleteAutoStaticCommand;

/**
 * @author jflute
 */
public class BatchDeleteEntityCommand extends AbstractListEntityCommand {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    /** The option of delete. (NotRequired) */
    protected DeleteOption<? extends ConditionBean> _deleteOption;

    // ===================================================================================
    //                                                                   Basic Information
    //                                                                   =================
    public String getCommandName() {
        return "batchDelete";
    }

    // ===================================================================================
    //                                                               SqlExecution Handling
    //                                                               =====================
    @Override
    public String buildSqlExecutionKey() {
        // same as super now, for the future
        return super.buildSqlExecutionKey();
    }

    public SqlExecutionCreator createSqlExecutionCreator() {
        assertStatus("createSqlExecutionCreator");
        return new SqlExecutionCreator() {
            public SqlExecution createSqlExecution() {
                final TnBeanMetaData bmd = createBeanMetaData();
                return createBatchDeleteEntitySqlExecution(bmd);
            }
        };
    }

    protected SqlExecution createBatchDeleteEntitySqlExecution(TnBeanMetaData bmd) {
        final String[] propertyNames = getPersistentPropertyNames(bmd);
        return createDeleteBatchAutoStaticCommand(bmd, propertyNames);
    }

    protected TnBatchDeleteAutoStaticCommand createDeleteBatchAutoStaticCommand(TnBeanMetaData bmd,
            String[] propertyNames) {
        final DBMeta dbmeta = findDBMeta();
        final boolean opt = isOptimisticLockHandling();
        return new TnBatchDeleteAutoStaticCommand(_dataSource, _statementFactory, bmd, dbmeta, propertyNames, opt,
                _deleteOption);
    }

    protected boolean isOptimisticLockHandling() {
        return true;
    }

    @Override
    protected Object[] doGetSqlExecutionArgument() {
        return new Object[] { _entityList }; // deleteOption is not specified because of static command
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public void setDeleteOption(DeleteOption<? extends ConditionBean> deleteOption) {
        _deleteOption = deleteOption;
    }
}
