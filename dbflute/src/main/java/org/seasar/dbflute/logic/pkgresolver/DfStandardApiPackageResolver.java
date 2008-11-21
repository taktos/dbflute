package org.seasar.dbflute.logic.pkgresolver;

import org.seasar.dbflute.properties.DfBasicProperties;

/**
 * @author jflute
 * @since 0.8.6 (2008/11/21 Friday)
 */
public class DfStandardApiPackageResolver {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected DfBasicProperties _basicProperties;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public DfStandardApiPackageResolver(DfBasicProperties basicProperties) {
        _basicProperties = basicProperties;
    }

    // ===================================================================================
    //                                                                            Resolver
    //                                                                            ========
    public String resolvePackageName(String typeName) { // [DBFLUTE-271]
        if (typeName == null) {
            return typeName;
        }
        final DfBasicProperties prop = _basicProperties;
        if (prop.isTargetLanguageJava()) {
            if (typeName.startsWith("List<") && typeName.endsWith(">")) {
                return "java.util." + typeName;
            }
            if (typeName.startsWith("Map<") && typeName.endsWith(">")) {
                return "java.util." + typeName;
            }
            if (typeName.equals("BigDecimal")) {
                return "java.math." + typeName;
            }
            if (typeName.equals("Time")) {
                return "java.sql." + typeName;
            }
            if (typeName.equals("Timestamp")) {
                return "java.sql." + typeName;
            }
            if (typeName.equals("Date")) {
                return "java.util." + typeName;
            }
        } else if (prop.isTargetLanguageCSharp()) {
            if (typeName.startsWith("IList<") && typeName.endsWith(">")) {
                return "System.Collections.Generic." + typeName;
            }
        }
        return typeName;
    }
}
