/*
 * Copyright 2004-2013 the Seasar Foundation and the Others.
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
package org.seasar.dbflute.cbean;

/**
 * The interface of scalar-query.
 * <pre>
 * fooBhv.scalarSelect(Date.class).max(new ScalarQuery&lt;FooCB&gt;() {
 *     public void query(FooCB cb) {
 *         cb.specify().columnFooDatetime(); <span style="color: #3F7E5E">// required for a function</span>
 *         cb.query().setFoo...
 *     }
 * }
 * </pre>
 * @author jflute
 * @param <CB> The type of condition-bean.
 */
public interface ScalarQuery<CB extends ConditionBean> {

    /**
     * Set up your query condition for scalar-query. <br />
     * Don't call the method 'setupSelect_Xxx()' and 'addOrderBy_Xxx...()'
     * and they are ignored if you call.
     * @param cb The condition-bean for scalar-query. (NotNull)
     */
    void query(CB cb);
}
