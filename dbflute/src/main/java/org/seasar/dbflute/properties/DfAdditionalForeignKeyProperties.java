package org.seasar.dbflute.properties;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;

/**
 * Build properties for Torque.
 * 
 * @author mkubo
 */
public final class DfAdditionalForeignKeyProperties extends DfAbstractHelperProperties {

    //    private static final Log _log = LogFactory.getLog(GeneratedClassPackageProperties.class);

    /**
     * Constructor.
     */
    public DfAdditionalForeignKeyProperties(Properties prop) {
        super(prop);
    }

    // ===============================================================================
    //                                               Properties - AdditionalForeignKey
    //                                               =================================
    public static final String KEY_additionalForeignKeyMap = "additionalForeignKeyMap";
    protected Map<String, Map<String, String>> _additionalForeignKeyMap;

    public Map<String, Map<String, String>> getAdditionalForeignKeyMap() {
        if (_additionalForeignKeyMap == null) {
            _additionalForeignKeyMap = new LinkedHashMap<String, Map<String, String>>();
            final Map<String, Object> generatedMap = mapProp("torque." + KEY_additionalForeignKeyMap, DEFAULT_EMPTY_MAP);
            final Set fisrtKeySet = generatedMap.keySet();
            for (Object foreignName : fisrtKeySet) {
                final Object firstValue = generatedMap.get(foreignName);
                if (!(firstValue instanceof Map)) {
                    String msg = "The value type should be Map: tableName=" + foreignName + " property=CustomizeDao";
                    msg = msg + " actualType=" + firstValue.getClass() + " actualValue=" + firstValue;
                    throw new IllegalStateException(msg);
                }
                final Map ForeignDefinitionMap = (Map) firstValue;
                Set secondKeySet = ForeignDefinitionMap.keySet();
                final Map<String, String> genericForeignDefinitiontMap = new LinkedHashMap<String, String>();
                for (Object componentName : secondKeySet) {
                    final Object secondValue = ForeignDefinitionMap.get(componentName);
                    if (secondValue == null) {
                        continue;
                    }
                    if (!(componentName instanceof String)) {
                        String msg = "The key type should be String: foreignName=" + foreignName
                                + " property=AdditionalForeignKey";
                        msg = msg + " actualType=" + componentName.getClass() + " actualKey=" + componentName;
                        throw new IllegalStateException(msg);
                    }
                    if (!(secondValue instanceof String)) {
                        String msg = "The value type should be String: foreignName=" + foreignName
                                + " property=AdditionalForeignKey";
                        msg = msg + " actualType=" + secondValue.getClass() + " actualValue=" + secondValue;
                        throw new IllegalStateException(msg);
                    }
                    genericForeignDefinitiontMap.put((String) componentName, (String) secondValue);
                }
                _additionalForeignKeyMap.put((String) foreignName, genericForeignDefinitiontMap);
            }
        }
        return _additionalForeignKeyMap;
    }

    public String getAdditionalForeignKeyComponentLocalTableName(String foreignName) {
        final Map<String, String> componentMap = getAdditionalForeignKeyMap().get(foreignName);
        return componentMap.get("localTableName");
    }

    public String getAdditionalForeignKeyComponentForeignTableName(String foreignName) {
        final Map<String, String> componentMap = getAdditionalForeignKeyMap().get(foreignName);
        return componentMap.get("foreignTableName");
    }

    protected String getComponentLocalColumnName(String foreignName) {
        final Map<String, String> componentMap = getAdditionalForeignKeyMap().get(foreignName);
        return componentMap.get("localColumnName");
    }

    protected Map<String, String> getComponentForeignCondition(String foreignName) {
        final Map<String, String> componentMap = getAdditionalForeignKeyMap().get(foreignName);
        final String foreignCondition = componentMap.get("foreignCondition");

        // TODO: Append ForeignCondition.
        return null;
    }

    public List<String> getComponentLocalColumnNameList(String foreignName) {
        final String property = getComponentLocalColumnName(foreignName);
        if (property == null || property.trim().length() == 0) {
            return null;
        }
        final List<String> localColumnNameList = new ArrayList<String>();
        final StringTokenizer st = new StringTokenizer(property, "/");
        while (st.hasMoreElements()) {
            localColumnNameList.add(st.nextToken());
        }
        return localColumnNameList;
    }

    protected String getComponentForeignColumnName(String foreignName) {
        final Map<String, String> componentMap = getAdditionalForeignKeyMap().get(foreignName);
        return componentMap.get("foreignColumnName");
    }

    public List<String> getComponentForeignColumnNameList(String foreignName) {
        final String property = getComponentForeignColumnName(foreignName);
        if (property == null || property.trim().length() == 0) {
            return null;
        }
        final List<String> foreignColumnNameList = new ArrayList<String>();
        final StringTokenizer st = new StringTokenizer(property, "/");
        while (st.hasMoreElements()) {
            foreignColumnNameList.add(st.nextToken());
        }
        return foreignColumnNameList;
    }
}