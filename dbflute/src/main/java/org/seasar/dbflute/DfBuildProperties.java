/*
 * Copyright 2004-2007 the Seasar Foundation and the Others.
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
package org.seasar.dbflute;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.seasar.dbflute.properties.DfAdditionalForeignKeyProperties;
import org.seasar.dbflute.properties.DfBasicProperties;
import org.seasar.dbflute.properties.DfClassificationProperties;
import org.seasar.dbflute.properties.DfCommonColumnProperties;
import org.seasar.dbflute.properties.DfDBFluteDiconProperties;
import org.seasar.dbflute.properties.DfGeneratedClassPackageProperties;
import org.seasar.dbflute.properties.DfIncludeQueryProperties;
import org.seasar.dbflute.properties.DfInvokeSqlDirectoryProperties;
import org.seasar.dbflute.properties.DfOptimisticLockProperties;
import org.seasar.dbflute.properties.DfOtherProperties;
import org.seasar.dbflute.properties.DfReplaceSchemaProperties;
import org.seasar.dbflute.properties.DfSelectParamProperties;
import org.seasar.dbflute.properties.DfSequenceIdentityProperties;
import org.seasar.dbflute.properties.DfSourceReductionProperties;
import org.seasar.dbflute.properties.DfSql2EntityProperties;
import org.seasar.dbflute.properties.handler.DfPropertiesHandler;
import org.seasar.dbflute.util.DfPropertyUtil;
import org.seasar.dbflute.util.DfPropertyUtil.PropertyBooleanFormatException;
import org.seasar.dbflute.util.DfPropertyUtil.PropertyNotFoundException;

/**
 * Build properties.
 * <pre>
 * Singletonでbuild.propertiesの情報を保持する。
 * ProcessのInitialize時にsetProperties()でPropertiesを設定されることが前提である。
 * </pre>
 * @author jflute
 */
public final class DfBuildProperties {

    /** Log-instance. */
    private static final Log _log = LogFactory.getLog(DfBuildProperties.class);

    /** Singleton-instance. */
    private static final DfBuildProperties _instance = new DfBuildProperties();

    /** The delimiter of map-list-string. */
    private static final String MAP_LIST_STRING_DELIMITER = ";";

    //========================================================================================
    //                                                                               Attribute
    //                                                                               =========
    /** Build properties. */
    private Properties _buildProperties;

    //========================================================================================
    //                                                                             Constructor
    //                                                                             ===========
    /**
     * Constructor. (Private for Singleton)
     */
    private DfBuildProperties() {
    }

    /**
     * Get singleton-instance.
     * 
     * @return Singleton-instance. (NotNull)
     */
    public synchronized static DfBuildProperties getInstance() {
        return _instance;
    }

    //========================================================================================
    //                                                                                Accessor
    //                                                                                ========
    /**
     * Set build-properties.
     * <pre>
     * This method should be invoked at first initialization.
     * Don't invoke with build-properties null.
     * </pre>
     * @param buildProperties Build-properties. (NotNull)
     */
    final public void setProperties(Properties buildProperties) {
        _buildProperties = buildProperties;
    }

    /**
     * Get Build-properties.
     * 
     * @return Build-properties. (NotNull)
     */
    final public Properties getProperties() {
        return _buildProperties;
    }

    // ★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★
    // TODO: @jflute - DfAbstractHelperPropertiesに移行中。移行完了後は削除
    //========================================================================================
    //                                                                        Property Utility
    //                                                                        ================
    /**
     * Get property as string. {Delegate method}
     * 
     * @param key Property-key. (NotNull)
     * @param defaultValue Default value. (Nullable)
     * @return Property as string. (Nullable: If the default-value is null)
     * @deprecated
     */
    final public String stringProp(String key, String defaultValue) {
        try {
            return DfPropertyUtil.stringProp(_buildProperties, key);
        } catch (PropertyNotFoundException e) {
            return defaultValue;
        } catch (RuntimeException e) {
            _log.warn("FlPropertyUtil#stringProp() threw the exception with The key[" + key + "]", e);
            throw e;
        }
    }

    /**
     * Get property as boolean. {Delegate method}
     * 
     * @param key Property-key. (NotNull)
     * @param defaultValue Default value.
     * @return Property as boolean.
     * @deprecated
     */
    final public boolean booleanProp(String key, boolean defaultValue) {
        try {
            return DfPropertyUtil.booleanProp(_buildProperties, key);
        } catch (PropertyNotFoundException e) {
            return defaultValue;
        } catch (PropertyBooleanFormatException e) {
            return defaultValue;
        } catch (RuntimeException e) {
            _log.warn("FlPropertyUtil#intProp() threw the exception with The key[" + key + "]", e);
            throw e;
        }
    }

    /**
     * Get property as list. {Delegate method}
     * 
     * @param key Property-key. (NotNull)
     * @param defaultValue Default value. (Nullable)
     * @return Property as list. (Nullable: If the default-value is null)
     * @deprecated
     */
    final public List<Object> listProp(String key, List<Object> defaultValue) {
        try {
            final List<Object> result = DfPropertyUtil.listProp(_buildProperties, key, MAP_LIST_STRING_DELIMITER);
            if (result.isEmpty()) {
                return defaultValue;
            } else {
                return result;
            }
        } catch (PropertyNotFoundException e) {
            return defaultValue;
        } catch (RuntimeException e) {
            _log.warn("FlPropertyUtil#intProp() threw the exception with The key[" + key + "]", e);
            throw e;
        }
    }

    /**
     * Get property as map. {Delegate method}
     * 
     * @param key Property-key. (NotNull)
     * @param defaultValue Default value. (Nullable)
     * @return Property as map. (Nullable: If the default-value is null)
     * @deprecated
     */
    final public Map<String, Object> mapProp(String key, Map<String, Object> defaultValue) {
        try {
            final Map<String, Object> result = DfPropertyUtil.mapProp(_buildProperties, key, MAP_LIST_STRING_DELIMITER);
            if (result.isEmpty()) {
                return defaultValue;
            } else {
                return result;
            }
        } catch (PropertyNotFoundException e) {
            return defaultValue;
        } catch (RuntimeException e) {
            _log.warn("FlPropertyUtil#intProp() threw the exception with The key[" + key + "]", e);
            throw e;
        }
    }

    // ★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★s

    //========================================================================================
    //                                                                      Default Definition
    //                                                                      ==================
    public static final Map<String, Object> DEFAULT_EMPTY_MAP = new LinkedHashMap<String, Object>();
    public static final List<Object> DEFAULT_EMPTY_LIST = new ArrayList<Object>();
    public static final String DEFAULT_EMPTY_MAP_STRING = "map:{}";
    public static final String DEFAULT_EMPTY_LIST_STRING = "list:{}";

    //========================================================================================
    //                                                                                 Handler
    //                                                                                 =======
    public DfPropertiesHandler getHandler() {
        return DfPropertiesHandler.getInstance();
    }

    //========================================================================================
    //                                                                         Property Object
    //                                                                         ===============
    // -----------------------------------------------------
    //                                                 Basic
    //                                                 -----
    public DfBasicProperties getBasicProperties() {
        return getHandler().getBasicProperties(getProperties());
    }

    // -----------------------------------------------------
    //                                              DaoDicon
    //                                              --------
    public DfDBFluteDiconProperties getDBFluteDiconProperties() {
        return getHandler().getDBFluteDiconProperties(getProperties());
    }

    // -----------------------------------------------------
    //                               Generated Class Package
    //                               -----------------------
    public DfGeneratedClassPackageProperties getGeneratedClassPackageProperties() {
        return getHandler().getGeneratedClassPackageProperties(getProperties());
    }

    // -----------------------------------------------------
    //                                 Sequence and Identity
    //                                 ---------------------
    public DfSequenceIdentityProperties getSequenceIdentityProperties() {
        return getHandler().getSequenceIdentityProperties(getProperties());
    }

    // -----------------------------------------------------
    //                                       Optimistic Lock
    //                                       ---------------
    public DfOptimisticLockProperties getOptimisticLockProperties() {
        return getHandler().getOptimisticLockProperties(getProperties());
    }

    // -----------------------------------------------------
    //                                         Common Column
    //                                         -------------
    public DfCommonColumnProperties getCommonColumnProperties() {
        return getHandler().getCommonColumnProperties(getProperties());
    }

    // -----------------------------------------------------
    //                                        Classification
    //                                        --------------
    protected DfClassificationProperties _classificationProperties;

    public DfClassificationProperties getClassificationProperties() {
        if (_classificationProperties == null) {
            _classificationProperties = new DfClassificationProperties(_buildProperties);
        }
        return _classificationProperties;
    }

    // -----------------------------------------------------
    //                                          Select Param
    //                                          ------------
    public DfSelectParamProperties getSelectParamProperties() {
        return getHandler().getSelectParamProperties(getProperties());
    }

    // -----------------------------------------------------
    //                                Additional Foreign-Key
    //                                ----------------------
    public DfAdditionalForeignKeyProperties getAdditionalForeignKeyProperties() {
        return getHandler().getAdditionalForeignKeyProperties(getProperties());
    }

    // -----------------------------------------------------
    //                              Original Behavior Aspect
    //                              ------------------------
    public static final String KEY_originalBehaviorAspectMap = "originalBehaviorAspectMap";
    protected Map<String, Map<String, String>> _originalBehaviorAspectMap;

    public Map<String, Map<String, String>> getOriginalBehaviorAspectMap() {
        if (_originalBehaviorAspectMap == null) {
            _originalBehaviorAspectMap = new LinkedHashMap<String, Map<String, String>>();

            final Map<String, Object> generatedMap = mapProp("torque." + KEY_originalBehaviorAspectMap,
                    DEFAULT_EMPTY_MAP);
            final Set<String> keySet = generatedMap.keySet();
            for (String key : keySet) {
                final Map<String, String> aspectDefinition = (Map<String, String>) generatedMap.get(key);
                _originalBehaviorAspectMap.put(key, aspectDefinition);
            }
        }
        return _originalBehaviorAspectMap;
    }

    public List<String> getOriginalBehaviorAspectComponentNameList() {
        return new ArrayList<String>(getOriginalBehaviorAspectMap().keySet());
    }

    public String getOriginalBehaviorAspectClassName(String componentName) {
        final Map<String, String> aspectDefinition = getOriginalBehaviorAspectMap().get(componentName);
        return aspectDefinition.get("className");
    }

    public String getOriginalBehaviorAspectPointcut(String componentName) {
        final Map<String, String> aspectDefinition = getOriginalBehaviorAspectMap().get(componentName);
        return aspectDefinition.get("pointcut");
    }

    // -----------------------------------------------------
    //                                        Extract Accept
    //                                        --------------
    public String getExtractAcceptStartBrace() {
        return stringProp("torque.extractAcceptStartBrace", "@{");
    }

    public String getExtractAcceptEndBrace() {
        return stringProp("torque.extractAcceptEndBrace", "@}");
    }

    public String getExtractAcceptDelimiter() {
        return stringProp("torque.extractAcceptDelimiter", "@;");
    }

    public String getExtractAcceptEqual() {
        return stringProp("torque.extractAcceptEqual", "@=");
    }

    // -----------------------------------------------------
    //                                      Source Reduction
    //                                      ----------------
    public DfSourceReductionProperties getSourceReductionProperties() {
        return getHandler().getSourceReductionProperties(getProperties());
    }

    public DfIncludeQueryProperties getIncludeQueryProperties() {
        return getHandler().getIncludeQueryProperties(getProperties());
    }

    // -----------------------------------------------------
    //                                                 Other
    //                                                 -----
    public DfOtherProperties getOtherProperties() {
        return getHandler().getOtherProperties(getProperties());
    }

    // TODO: @jflute - 削除予定。今後DBFluteではサポートしない。
    // ★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★
    // ===============================================================================
    //                                                       Properties - CustomizeDao
    //                                                       =========================
    public static final String KEY_customizeDaoDefinitionMap = "customizeDaoDefinitionMap";
    protected Map<String, Map<String, Map<String, String>>> _customizeDaoDefinitionMap;

    public Map<String, Map<String, Map<String, String>>> getCustomizeDaoDifinitionMap() {
        if (_customizeDaoDefinitionMap == null) {
            _customizeDaoDefinitionMap = new LinkedHashMap<String, Map<String, Map<String, String>>>();
            final Map<String, Object> generatedMap = mapProp("torque." + KEY_customizeDaoDefinitionMap,
                    DEFAULT_EMPTY_MAP);
            final Set fisrtKeySet = generatedMap.keySet();
            for (Object tableName : fisrtKeySet) {
                final Object firstValue = generatedMap.get(tableName);
                if (!(firstValue instanceof Map)) {
                    String msg = "The value type should be Map: tableName=" + tableName + " property=CustomizeDao";
                    msg = msg + " actualType=" + firstValue.getClass() + " actualValue=" + firstValue;
                    throw new IllegalStateException(msg);
                }
                final Map tableDefinitionMap = (Map) firstValue;
                Set secondKeySet = tableDefinitionMap.keySet();
                final Map<String, Map<String, String>> genericTableDefinitiontMap = new LinkedHashMap<String, Map<String, String>>();
                for (Object componentName : secondKeySet) {
                    final Object secondValue = tableDefinitionMap.get(componentName);
                    if (secondValue == null) {
                        continue;
                    }
                    if (!(componentName instanceof String)) {
                        String msg = "The key type should be String: tableName=" + tableName + " property=CustomizeDao";
                        msg = msg + " actualType=" + componentName.getClass() + " actualKey=" + componentName;
                        throw new IllegalStateException(msg);
                    }
                    if (!(secondValue instanceof Map)) {
                        String msg = "The value type should be Map: tableName=" + tableName + " property=CustomizeDao";
                        msg = msg + " actualType=" + secondValue.getClass() + " actualValue=" + secondValue;
                        throw new IllegalStateException(msg);
                    }

                    final Map componentMap = (Map) secondValue;
                    Set thirdKeySet = componentMap.keySet();
                    final Map<String, String> genericComponentMap = new LinkedHashMap<String, String>();
                    for (Object componentKey : thirdKeySet) {
                        final Object componentValue = componentMap.get(componentKey);
                        if (!(componentKey instanceof String)) {
                            String msg = "The key type should be String: tableName=" + tableName
                                    + " property=CustomizeDao";
                            msg = msg + " actualType=" + componentKey.getClass() + " actualKey=" + componentKey;
                            throw new IllegalStateException(msg);
                        }
                        if (!(componentValue instanceof String)) {
                            String msg = "The value type should be String: tableName=" + tableName
                                    + " property=CustomizeDao";
                            msg = msg + " actualType=" + componentValue.getClass() + " actualValue=" + componentValue;
                            throw new IllegalStateException(msg);
                        }
                        genericComponentMap.put((String) componentKey, (String) componentValue);
                    }
                    genericTableDefinitiontMap.put((String) componentName, genericComponentMap);
                }
                _customizeDaoDefinitionMap.put((String) tableName, genericTableDefinitiontMap);
            }
        }
        return _customizeDaoDefinitionMap;
    }

    public Map<String, String> getCustomizeDaoComponentColumnMap(String tableName) {
        final Map<String, Map<String, String>> componentDefinitionMap = getCustomizeDaoDifinitionMap().get(tableName);
        final Map<String, String> columnMap = componentDefinitionMap.get("columnMap");
        if (columnMap == null) {
            String msg = "The table did not have 'columnMap': tableName=" + tableName;
            msg = msg + " componentDefinitionMap=" + componentDefinitionMap;
            msg = msg + " " + KEY_customizeDaoDefinitionMap + "=" + getCustomizeDaoDifinitionMap();
            throw new IllegalStateException(msg);
        }
        return columnMap;
    }

    public Map<String, String> getCustomizeDaoComponentMethodMap(String tableName) {
        final Map<String, Map<String, String>> componentDefinitionMap = getCustomizeDaoDifinitionMap().get(tableName);
        final Map<String, String> methodMap = componentDefinitionMap.get("methodMap");
        if (methodMap == null) {
            String msg = "The table did not have 'methodMap': tableName=" + tableName;
            msg = msg + " componentDefinitionMap=" + componentDefinitionMap;
            msg = msg + " " + KEY_customizeDaoDefinitionMap + "=" + getCustomizeDaoDifinitionMap();
            throw new IllegalStateException(msg);
        }
        return methodMap;
    }

    public String getCustomizeDaoComponentMethodArgumentVariableCommaString(String tableName, String methodName) {
        final Map<String, String> methodMap = getCustomizeDaoComponentMethodMap(tableName);
        final StringBuffer sb = new StringBuffer();
        final String argumentString = methodMap.get(methodName);
        final StringTokenizer st = new StringTokenizer(argumentString, ",");
        while (st.hasMoreTokens()) {
            final String trimmedToken = st.nextToken().trim();
            if (trimmedToken.indexOf(" ") == -1) {
                String msg = "The trimmedToken should have one blank: trimmedToken" + trimmedToken + " methodMap="
                        + methodMap;
                throw new IllegalStateException(msg);
            }
            sb.append(", ").append(trimmedToken.substring(trimmedToken.indexOf(" ") + 1));
        }
        sb.delete(0, ", ".length());
        return sb.toString();
    }

    public Map<String, String> getCustomizeDaoComponentImportMap(String tableName) {
        final Map<String, Map<String, String>> componentDefinitionMap = getCustomizeDaoDifinitionMap().get(tableName);
        return componentDefinitionMap.get("importMap");
    }

    public Map<String, String> getCustomizeDaoComponentRelationMap(String tableName) {
        final Map<String, Map<String, String>> componentDefinitionMap = getCustomizeDaoDifinitionMap().get(tableName);
        return componentDefinitionMap.get("relationMap");
    }

    public boolean isAvailableCustomizeDaoGeneration() {
        return !getCustomizeDaoDifinitionMap().isEmpty();
    }

    // ★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★

    // TODO: @jflute - 削除予定。今後DBFluteではサポートしない。
    // ★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★
    // ===============================================================================
    //                                                   Properties - SqlParameterBean
    //                                                   =============================
    public static final String KEY_sqlParameterBeanDefinitionMap = "sqlParameterBeanDefinitionMap";
    protected Map<String, Object> _sqlParameterBeanDefinitionMap;

    public String getSqlParameterBeanPackage() {
        return stringProp("torque.sqlParameterBeanPackage", "");
    }

    public Map<String, Object> getSqlParameterBeanDefinitionMap() {
        if (_sqlParameterBeanDefinitionMap == null) {
            _sqlParameterBeanDefinitionMap = mapProp("torque." + KEY_sqlParameterBeanDefinitionMap, DEFAULT_EMPTY_MAP);
        }
        return _sqlParameterBeanDefinitionMap;
    }

    public List<String> getSqlParameterBeanClassNameList() {
        return new ArrayList<String>(getSqlParameterBeanDefinitionMap().keySet());
    }

    public Map<String, String> getSqlParameterBeanClassDefinitionMap(String className) {
        final Map<String, String> map = (Map<String, String>) getSqlParameterBeanDefinitionMap().get(className);
        if (map == null) {
            String msg = "getSqlParameterBeanDefinitionMap().get(className) returned null: " + className;
            throw new IllegalArgumentException(msg);
        }
        return map;
    }

    public String getSqlParameterBeanPropertyType(String className, String property) {
        final String str = (String) getSqlParameterBeanClassDefinitionMap(className).get(property);
        if (str == null) {
            String msg = "getSqlParameterBeanClassDefinitionMap(className).get(property) returned null";
            msg = msg + ": className=" + className + " property=" + property;
            throw new IllegalArgumentException(msg);
        }
        if (str.indexOf("-") == -1) {
            return str;
        }
        return str.substring(0, str.indexOf("-"));
    }

    public boolean isSqlParameterBeanPropertyDefaultValueEffective(String className, String property) {
        return getSqlParameterBeanPropertyDefaultValue(className, property).trim().length() != 0;
    }

    public String getSqlParameterBeanPropertyDefaultValue(String className, String property) {
        final String str = (String) getSqlParameterBeanClassDefinitionMap(className).get(property);
        if (str == null) {
            String msg = "getSqlParameterBeanClassDefinitionMap(className).get(property) returned null";
            msg = msg + ": className=" + className + " property=" + property;
            throw new IllegalArgumentException(msg);
        }
        if (str.indexOf("-") == -1) {
            return "";
        }
        return str.substring(str.indexOf("-") + 1);
    }

    public boolean isAvailableSqlParameterBeanGeneration() {
        return !getSqlParameterBeanDefinitionMap().isEmpty();
    }

    public boolean isSqlParameterBeanHaveTheProperty(String className, String property) {
        final String str = (String) getSqlParameterBeanClassDefinitionMap(className).get(property);
        return str != null;
    }

    // ★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★

    // TODO: @jflute - 削除予定。今後DBFluteではサポートしない。
    // ★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★
    // ===============================================================================
    //                                                       Properties - ArguemntBean
    //                                                       =========================
    public static final String KEY_argumentBeanDefinitionMap = "argumentBeanDefinitionMap";
    protected Map<String, Object> _argumentBeanDefinitionMap;

    public String getArgumentBeanPackage() {
        return stringProp("torque.argumentBeanPackage", "");
    }

    public Map<String, Object> getArgumentBeanDefinitionMap() {
        if (_argumentBeanDefinitionMap == null) {
            _argumentBeanDefinitionMap = mapProp("torque." + KEY_argumentBeanDefinitionMap, DEFAULT_EMPTY_MAP);
        }
        return _argumentBeanDefinitionMap;
    }

    public List<String> getArgumentBeanClassNameList() {
        return new ArrayList<String>(getArgumentBeanDefinitionMap().keySet());
    }

    public Map<String, String> getArgumentBeanClassDefinitionMap(String className) {
        final Map<String, String> map = (Map<String, String>) getArgumentBeanDefinitionMap().get(className);
        if (map == null) {
            String msg = "getArgumentBeanDifinitionMap().get(className) returned null: " + className;
            throw new IllegalArgumentException(msg);
        }
        return map;
    }

    public String getArgumentBeanPropertyType(String className, String property) {
        final String str = (String) getArgumentBeanClassDefinitionMap(className).get(property);
        if (str == null) {
            String msg = "getArgumentBeanClassDefinitionMap(className).get(property) returned null";
            msg = msg + ": className=" + className + " property=" + property;
            throw new IllegalArgumentException(msg);
        }
        if (str.indexOf("-") == -1) {
            return str;
        }
        return str.substring(0, str.indexOf("-"));
    }

    public boolean isArgumentBeanPropertyDefaultValueEffective(String className, String property) {
        return getArgumentBeanPropertyDefaultValue(className, property).trim().length() != 0;
    }

    public String getArgumentBeanPropertyDefaultValue(String className, String property) {
        final String str = (String) getArgumentBeanClassDefinitionMap(className).get(property);
        if (str == null) {
            String msg = "getArgumentBeanClassDefinitionMap(className).get(property) returned null";
            msg = msg + ": className=" + className + " property=" + property;
            throw new IllegalArgumentException(msg);
        }
        if (str.indexOf("-") == -1) {
            return "";
        }
        return str.substring(str.indexOf("-") + 1);
    }

    public boolean isAvailableArgumentBeanGeneration() {
        return !getArgumentBeanDefinitionMap().isEmpty();
    }

    public static final String KEY_argumentBeanRelatedSqlParameterMap = "argumentBeanRelatedSqlParameterMap";
    protected Map<String, Object> _argumentBeanRelatedSqlParameterMap;

    public Map<String, Object> getArgumentBeanRelatedSqlParameterMap() {
        if (_argumentBeanRelatedSqlParameterMap == null) {
            _argumentBeanRelatedSqlParameterMap = mapProp("torque." + KEY_argumentBeanRelatedSqlParameterMap,
                    DEFAULT_EMPTY_MAP);
        }
        return _argumentBeanRelatedSqlParameterMap;
    }

    public List<String> getArgumentBeanRelatedSqlParameterSqlParameterNameList(String argumentBeanName) {
        return (List<String>) getArgumentBeanRelatedSqlParameterMap().get(argumentBeanName);
    }

    // ★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★

    // -----------------------------------------------------
    //                                          Table Except
    //                                          ------------
    protected List<String> _tableExceptList;

    public List<String> getTableExceptList() {
        if (_tableExceptList == null) {
            final List<Object> tmpLs = listProp("torque.table.except.list", DEFAULT_EMPTY_LIST);
            _tableExceptList = new ArrayList<String>();
            for (Object object : tmpLs) {
                _tableExceptList.add((String) object);
            }
            _tableExceptList.addAll(getTableExceptInformation().getTableExceptList());
        }
        return _tableExceptList;
    }

    protected TableExceptInformation _tableExceptInformation;

    public TableExceptInformation getTableExceptInformation() {
        if (_tableExceptInformation == null) {
            if ("mssql".equals(getBasicProperties().getDatabaseName())) {
                _tableExceptInformation = new TableExceptSQLServer();
            } else {
                _tableExceptInformation = new TableExceptDefault();
            }
        }
        return _tableExceptInformation;
    }

    public static interface TableExceptInformation {
        public List<String> getTableExceptList();
    }

    public static class TableExceptSQLServer implements TableExceptInformation {
        public List<String> getTableExceptList() {
            return Arrays.asList(new String[] { "sysconstraints", "syssegments", "dtproperties" });
        }
    }

    public static class TableExceptDefault implements TableExceptInformation {
        public List<String> getTableExceptList() {
            return Arrays.asList(new String[] {});
        }
    }

    // -----------------------------------------------------
    //                           jdbcToJavaNative (Internal)
    //                           ---------------------------
    public String getJdbcToJavaNativeAsStringRemovedLineSeparator() {
        final String property = stringProp("torque.jdbcToJavaNativeMap", DEFAULT_EMPTY_MAP_STRING);
        return removeNewLine(property);
    }

    protected Map<String, Object> _jdbcToJavaNativeMap;

    public Map<String, Object> getJdbcToJavaNative() {
        if (_jdbcToJavaNativeMap == null) {
            _jdbcToJavaNativeMap = mapProp("torque.jdbcToJavaNativeMap", getLanguageMetaData().getJdbcToJavaNativeMap());
        }
        return _jdbcToJavaNativeMap;
    }

    protected List<Object> _javaNativeStringList;

    public List<Object> getJavaNativeStringList() {
        if (_javaNativeStringList == null) {
            _javaNativeStringList = listProp("torque.javaNativeStringList", getLanguageMetaData().getStringList());
        }
        return _javaNativeStringList;
    }

    protected List<Object> _javaNativeBooleanList;

    public List<Object> getJavaNativeBooleanList() {
        if (_javaNativeBooleanList == null) {
            _javaNativeBooleanList = listProp("torque.javaNativeBooleanList", getLanguageMetaData().getBooleanList());
        }
        return _javaNativeBooleanList;
    }

    protected List<Object> _javaNativeNumberList;

    public List<Object> getJavaNativeNumberList() {
        if (_javaNativeNumberList == null) {
            _javaNativeNumberList = listProp("torque.javaNativeNumberList", getLanguageMetaData().getNumberList());
        }
        return _javaNativeNumberList;
    }

    protected List<Object> _javaNativeDateList;

    public List<Object> getJavaNativeDateList() {
        if (_javaNativeDateList == null) {
            _javaNativeDateList = listProp("torque.javaNativeDateList", getLanguageMetaData().getDateList());
        }
        return _javaNativeDateList;
    }

    protected List<Object> _javaNativeBinaryList;

    public List<Object> getJavaNativeBinaryList() {
        if (_javaNativeBinaryList == null) {
            _javaNativeBinaryList = listProp("torque.javaNativeBinaryList", getLanguageMetaData().getBinaryList());
        }
        return _javaNativeBinaryList;

    }

    protected LanguageMetaData _languageMetaData;

    protected LanguageMetaData getLanguageMetaData() {
        if (getBasicProperties().isTargetLanguageJava()) {
            if (_languageMetaData == null) {
                _languageMetaData = new JavaMetaData();
            }
        } else if (getBasicProperties().isTargetLanguageCSharp()) {
            if (_languageMetaData == null) {
                _languageMetaData = new CSharpMetaData();
            }
        } else {
            String msg = "The language is unsupported: " + getBasicProperties().getTargetLanguage();
            throw new IllegalStateException(msg);
        }
        return _languageMetaData;
    }

    public static interface LanguageMetaData {

        public Map<String, Object> getJdbcToJavaNativeMap();

        public List<Object> getStringList();

        public List<Object> getBooleanList();

        public List<Object> getNumberList();

        public List<Object> getDateList();

        public List<Object> getBinaryList();
    }

    public static class JavaMetaData implements LanguageMetaData {
        public Map<String, Object> getJdbcToJavaNativeMap() {
            return DEFAULT_EMPTY_MAP;
        }

        public List<Object> getStringList() {
            return Arrays.asList(new Object[] { "String" });
        }

        public List<Object> getBooleanList() {
            return Arrays.asList(new Object[] { "Boolean" });
        }

        public List<Object> getNumberList() {
            return Arrays.asList(new Object[] { "Byte", "Short", "Integer", "Long", "Float", "Double", "BigDecimal",
                    "BigInteger" });
        }

        public List<Object> getDateList() {
            return Arrays.asList(new Object[] { "Date", "Time", "Timestamp" });
        }

        public List<Object> getBinaryList() {
            return Arrays.asList(new Object[] { "byte[]" });
        }
    }

    public static class CSharpMetaData implements LanguageMetaData {
        public Map<String, Object> getJdbcToJavaNativeMap() {
            final Map<String, Object> map = new LinkedHashMap<String, Object>();
            map.put("CHAR", "String");
            map.put("VARCHAR", "String");
            map.put("LONGVARCHAR", "String");
            map.put("NUMERIC", "Nullables.NullableDecimal");
            map.put("DECIMAL", "Nullables.NullableDecimal");
            map.put("BIT", "Nullables.NullableBoolean");
            map.put("TINYINT", "Nullables.NullableDecimal");
            map.put("SMALLINT", "Nullables.NullableDecimal");
            map.put("INTEGER", "Nullables.NullableDecimal");
            map.put("BIGINT", "Nullables.NullableDecimal");
            map.put("REAL", "Nullables.NullableDecimal");
            map.put("FLOAT", "Nullables.NullableDecimal");
            map.put("DOUBLE", "Nullables.NullableDecimal");
            map.put("DATE", "Nullables.NullableDateTime");
            map.put("TIME", "Nullables.NullableDateTime");
            map.put("TIMESTAMP", "Nullables.NullableDateTime");
            return map;
        }

        public List<Object> getStringList() {
            return Arrays.asList(new Object[] { "String" });
        }

        public List<Object> getBooleanList() {
            return Arrays.asList(new Object[] { "Nullables.NullableBoolean" });
        }

        public List<Object> getNumberList() {
            return Arrays.asList(new Object[] { "Nullables.NullableDecimal" });
        }

        public List<Object> getDateList() {
            return Arrays.asList(new Object[] { "Nullables.NullableDateTime" });
        }

        public List<Object> getBinaryList() {
            return Arrays.asList(new Object[] { "byte[]" });
        }
    }

    // -----------------------------------------------------
    //         ToLowerInGeneratorUnderscoreMethod (Internal)
    //         ---------------------------------------------
    public boolean isAvailableToLowerInGeneratorUnderscoreMethod() {
        return booleanProp("torque.isAvailableToLowerInGeneratorUnderscoreMethod", true);
    }

    // -----------------------------------------------------
    //                      invokeReplaceSchemaDefinitionMap
    //                      --------------------------------
    public DfReplaceSchemaProperties getInvokeReplaceSchemaProperties() {
        return getHandler().getReplaceSchemaProperties(getProperties());
    }

    // -----------------------------------------------------
    //                       invokeSqlDirectoryDefinitionMap
    //                       -------------------------------
    public DfInvokeSqlDirectoryProperties getInvokeSqlDirectoryProperties() {
        return getHandler().getInvokeSqlDirectoryProperties(getProperties());
    }

    // -----------------------------------------------------
    //                               sql2EntityDefinitionMap
    //                               -----------------------
    public DfSql2EntityProperties getSql2EntityProperties() {
        return getHandler().getSql2EntityProperties(getProperties());
    }

    // ===============================================================================
    //                                                                          Helper
    //                                                                          ======
    private String removeNewLine(String str) {
        return DfPropertyUtil.removeAll(str, System.getProperty("line.separator"));
    }
}