/*
 * Copyright 2004-2006 the Seasar Foundation and the Others.
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
package org.seasar.dbflute.logic.replaceschema.loaddata.impl;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.torque.engine.database.model.UnifiedSchema;
import org.seasar.dbflute.exception.DfTableDataRegistrationFailureException;
import org.seasar.dbflute.helper.StringKeyMap;
import org.seasar.dbflute.logic.replaceschema.loaddata.DfDelimiterDataHandler;
import org.seasar.dbflute.logic.replaceschema.loaddata.DfDelimiterDataResource;
import org.seasar.dbflute.logic.replaceschema.loaddata.DfDelimiterDataResultInfo;
import org.seasar.dbflute.logic.replaceschema.loaddata.DfLoadedDataInfo;
import org.seasar.dbflute.logic.replaceschema.loaddata.interceotpr.DfDataWritingInterceptor;
import org.seasar.dbflute.properties.filereader.DfMapStringFileReader;

/**
 * @author jflute
 */
public class DfDelimiterDataHandlerImpl implements DfDelimiterDataHandler {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    /** Log instance. */
    private static final Log _log = LogFactory.getLog(DfDelimiterDataHandlerImpl.class);

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected boolean _loggingInsertSql;
    protected DataSource _dataSource;
    protected UnifiedSchema _unifiedSchema;
    protected boolean _suppressBatchUpdate;
    protected DfDataWritingInterceptor _dataWritingInterceptor;

    // ===================================================================================
    //                                                                                Main
    //                                                                                ====
    public DfDelimiterDataResultInfo writeSeveralData(DfDelimiterDataResource resource, DfLoadedDataInfo loadedDataInfo) {
        final DfDelimiterDataResultInfo resultInfo = new DfDelimiterDataResultInfo();
        final Map<String, Set<String>> notFoundColumnMap = resultInfo.getNotFoundColumnMap();
        final String basePath = resource.getBasePath();
        final File baseDir = new File(basePath);
        final String[] dataDirectoryElements = baseDir.list(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return !name.startsWith(".");
            }
        });
        if (dataDirectoryElements == null) {
            return resultInfo;
        }
        final FilenameFilter filter = createFilenameFilter(resource.getFileType());

        try {
            for (String encoding : dataDirectoryElements) {
                if (isUnsupportedEncodingDirectory(encoding)) {
                    _log.warn("The encoding(directory name) is unsupported: encoding=" + encoding);
                    continue;
                }

                final File encodingNameDirectory = new File(basePath + "/" + encoding);
                final String[] fileNameList = encodingNameDirectory.list(filter);

                final Comparator<String> fileNameAscComparator = new Comparator<String>() {
                    public int compare(String o1, String o2) {
                        return o1.compareTo(o2);
                    }
                };
                final SortedSet<String> sortedFileNameSet = new TreeSet<String>(fileNameAscComparator);
                for (String fileName : fileNameList) {
                    sortedFileNameSet.add(fileName);
                }

                final Map<String, Map<String, String>> convertValueMap = getConvertValueMap(resource, encoding);
                final Map<String, String> defaultValueMap = getDefaultValueMap(resource, encoding);
                for (String fileName : sortedFileNameSet) {
                    final String fileNamePath = basePath + "/" + encoding + "/" + fileName;
                    final DfDelimiterDataWriterImpl writer = new DfDelimiterDataWriterImpl(_dataSource);
                    writer.setUnifiedSchema(_unifiedSchema);
                    writer.setLoggingInsertSql(isLoggingInsertSql());
                    writer.setFileName(fileNamePath);
                    writer.setEncoding(encoding);
                    writer.setDelimiter(resource.getDelimiter());
                    writer.setConvertValueMap(convertValueMap);
                    writer.setDefaultValueMap(defaultValueMap);
                    writer.setSuppressBatchUpdate(isSuppressBatchUpdate());
                    writer.setDataWritingInterceptor(_dataWritingInterceptor);
                    writer.writeData(notFoundColumnMap);
                    loadedDataInfo.addLoadedFile(resource.getEnvType(), resource.getFileType(), encoding, fileName);
                }
            }
        } catch (IOException e) {
            String msg = "Failed to register delimiter data.";
            throw new DfTableDataRegistrationFailureException(msg, e);
        }
        return resultInfo;
    }

    protected boolean isUnsupportedEncodingDirectory(String encoding) {
        try {
            new String(new byte[0], 0, 0, encoding);
            return false;
        } catch (UnsupportedEncodingException e) {
            return true;
        }
    }

    private Map<String, Map<String, String>> getConvertValueMap(DfDelimiterDataResource info, String encoding) {
        final DfMapStringFileReader reader = new DfMapStringFileReader();
        String path = info.getBasePath() + "/" + encoding + "/convertValueMap.dataprop";
        final Map<String, Map<String, String>> resultMap = StringKeyMap.createAsFlexibleOrdered();
        Map<String, Map<String, String>> readMap = reader.readMapAsStringMapValue(path);
        if (readMap != null && !readMap.isEmpty()) {
            resultMap.putAll(readMap);
            return resultMap;
        }
        path = info.getBasePath() + "/" + encoding + "/convert-value.txt";
        readMap = reader.readMapAsStringMapValue(path);
        resultMap.putAll(readMap);
        return resultMap;
    }

    private Map<String, String> getDefaultValueMap(DfDelimiterDataResource info, String encoding) {
        final DfMapStringFileReader reader = new DfMapStringFileReader();
        String path = info.getBasePath() + "/" + encoding + "/defaultValueMap.dataprop";
        final Map<String, String> resultMap = StringKeyMap.createAsFlexibleOrdered();
        Map<String, String> readMap = reader.readMapAsStringValue(path);
        if (readMap != null && !readMap.isEmpty()) {
            resultMap.putAll(readMap);
            return resultMap;
        }
        path = info.getBasePath() + "/" + encoding + "/default-value.txt";
        readMap = reader.readMapAsStringValue(path);
        resultMap.putAll(readMap);
        return resultMap;
    }

    protected FilenameFilter createFilenameFilter(final String typeName) {
        final FilenameFilter filter = new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.endsWith("." + typeName);
            }
        };
        return filter;
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public boolean isLoggingInsertSql() {
        return _loggingInsertSql;
    }

    public void setLoggingInsertSql(boolean loggingInsertSql) {
        this._loggingInsertSql = loggingInsertSql;
    }

    public DataSource getDataSource() {
        return _dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this._dataSource = dataSource;
    }

    public UnifiedSchema getUnifiedSchema() {
        return _unifiedSchema;
    }

    public void setUnifiedSchema(UnifiedSchema unifiedSchema) {
        this._unifiedSchema = unifiedSchema;
    }

    public boolean isSuppressBatchUpdate() {
        return _suppressBatchUpdate;
    }

    public void setSuppressBatchUpdate(boolean suppressBatchUpdate) {
        this._suppressBatchUpdate = suppressBatchUpdate;
    }

    public DfDataWritingInterceptor getDataWritingInterceptor() {
        return _dataWritingInterceptor;
    }

    public void setDataWritingInterceptor(DfDataWritingInterceptor dataWritingInterceptor) {
        this._dataWritingInterceptor = dataWritingInterceptor;
    }
}
