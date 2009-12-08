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
package org.seasar.dbflute.logic.jdbc.metadata.info;

/**
 * @author jflute
 * @since 0.9.6.2 (2009/12/08 Tuesday)
 */
public class DfProcedureSynonymMetaInfo {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected DfSynonymMetaInfo _synonymMetaInfo;
    protected DfProcedureMetaInfo _procedureMetaInfo;

    // ===================================================================================
    //                                                                              Switch
    //                                                                              ======
    public void reflectSynonymToProcedure() {
        if (_procedureMetaInfo == null) {
            String msg = "The procedureMetaInfo should not be null!";
            throw new IllegalStateException(msg);
        }
        if (_synonymMetaInfo == null) {
            String msg = "The synonymMetaInfo should not be null!";
            throw new IllegalStateException(msg);
        }
        _procedureMetaInfo.setProcedureCatalog(null);
        _procedureMetaInfo.setProcedureSchema(_synonymMetaInfo.getSynonymOwner());
        _procedureMetaInfo.setProcedureName(_synonymMetaInfo.getSynonymName());
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public DfSynonymMetaInfo getSynonymMetaInfo() {
        return _synonymMetaInfo;
    }

    public void setSynonymMetaInfo(DfSynonymMetaInfo synonymMetaInfo) {
        this._synonymMetaInfo = synonymMetaInfo;
    }

    public DfProcedureMetaInfo getProcedureMetaInfo() {
        return _procedureMetaInfo;
    }

    public void setProcedureMetaInfo(DfProcedureMetaInfo procedureMetaInfo) {
        this._procedureMetaInfo = procedureMetaInfo;
    }
}
