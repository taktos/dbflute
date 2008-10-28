package org.seasar.dbflute.helper.dataset.types;

/**
 * Data Table. {Refer to S2Container}
 * @author jflute
 * @since 0.8.3 (2008/10/28 Tuesday)
 */
public class ObjectType implements ColumnType {

    public ObjectType() {
    }

    public Object convert(Object value, String formatPattern) {
        return value;
    }

    public boolean equals(Object arg1, Object arg2) {
        if (arg1 == null) {
            return arg2 == null;
        }
        return doEquals(arg1, arg2);
    }

    @SuppressWarnings("unchecked")
    protected boolean doEquals(Object arg1, Object arg2) {
        try {
            arg1 = convert(arg1, null);
        } catch (Throwable t) {
            return false;
        }
        try {
            arg2 = convert(arg2, null);
        } catch (Throwable t) {
            return false;
        }
        if ((arg1 instanceof Comparable) && (arg2 instanceof Comparable)) {
            return ((Comparable) arg1).compareTo(arg2) == 0;
        }
        return arg1.equals(arg2);
    }

    public Class<?> getType() {
        return Object.class;
    }
}