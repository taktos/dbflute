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
package org.seasar.dbflute.s2dao.sqlcommand;

import javax.sql.DataSource;

import org.seasar.dbflute.jdbc.StatementFactory;
import org.seasar.dbflute.resource.ResourceContext;
import org.seasar.dbflute.s2dao.sqlhandler.TnCommandContextHandler;
import org.seasar.dbflute.twowaysql.SqlAnalyzer;
import org.seasar.dbflute.twowaysql.context.CommandContext;
import org.seasar.dbflute.twowaysql.context.CommandContextCreator;
import org.seasar.dbflute.twowaysql.node.Node;

/**
 * {Created with reference to S2Container's utility and extended for DBFlute}
 * @author jflute
 */
public abstract class TnAbstractQueryDynamicCommand extends TnAbstractBasicSqlCommand {

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public TnAbstractQueryDynamicCommand(DataSource dataSource, StatementFactory statementFactory) {
        super(dataSource, statementFactory);
    }

    // ===================================================================================
    //                                                                      CommandContext
    //                                                                      ==============
    protected TnCommandContextHandler createCommandContextHandler(CommandContext context) {
        return new TnCommandContextHandler(_dataSource, _statementFactory, context);
    }

    protected CommandContext createCommandContext(String twoWaySql, String[] argNames, Class<?>[] argTypes,
            Object[] args) {
        final CommandContext context;
        {
            final SqlAnalyzer analyzer = createSqlAnalyzer(twoWaySql);
            final Node node = analyzer.analyze();
            final CommandContextCreator creator = new CommandContextCreator(argNames, argTypes);
            context = creator.createCommandContext(args);
            node.accept(context);
        }
        return context;
    }

    protected SqlAnalyzer createSqlAnalyzer(String sql) {
        return ResourceContext.createSqlAnalyzer(sql, true);
    }
}
