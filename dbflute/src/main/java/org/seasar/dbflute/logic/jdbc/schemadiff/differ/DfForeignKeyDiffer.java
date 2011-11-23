package org.seasar.dbflute.logic.jdbc.schemadiff.differ;

import java.util.List;

import org.apache.torque.engine.database.model.ForeignKey;
import org.apache.torque.engine.database.model.Table;
import org.seasar.dbflute.logic.jdbc.schemadiff.DfForeignKeyDiff;
import org.seasar.dbflute.logic.jdbc.schemadiff.DfNextPreviousDiff;
import org.seasar.dbflute.logic.jdbc.schemadiff.DfTableDiff;
import org.seasar.dbflute.util.DfCollectionUtil;
import org.seasar.dbflute.util.Srl;

/**
 * @author jflute
 */
public class DfForeignKeyDiffer extends DfBasicConstraintKeyDiffer<ForeignKey, DfForeignKeyDiff> {

    public DfForeignKeyDiffer(DfTableDiff tableDiff) {
        super(tableDiff);
    }

    public String constraintName(ForeignKey key) {
        return key.getName();
    }

    public List<ForeignKey> keyList(Table table) {
        return DfCollectionUtil.newArrayList(table.getForeignKeys());
    }

    public String column(ForeignKey key) {
        return key.getLocalColumnNameCommaString();
    }

    @Override
    public boolean isAutoGeneratedName(String name) {
        if (isDatabaseMySQL()) {
            if (name != null && Srl.containsIgnoreCase(name, "_ibfk_")) {
                return true;
            }
        }
        return super.isAutoGeneratedName(name);
    }

    @Override
    public boolean isSameStructure(ForeignKey next, ForeignKey previous) {
        if (isSame(column(next), column(previous))) {
            if (isSame(next.getForeignTable().getName(), previous.getForeignTable().getName())) {
                return true;
            }
        }
        return false;
    }

    public void diff(DfForeignKeyDiff diff, ForeignKey nextKey, ForeignKey previousKey) {
        // foreignTable
        if (nextKey != null && previousKey != null) { // means change
            final String nextFKTable = nextKey.getForeignTableName();
            final String previousFKTable = previousKey.getForeignTableName();
            if (!isSame(nextFKTable, previousFKTable)) {
                final DfNextPreviousDiff fkTableDiff = createNextPreviousDiff(nextFKTable, previousFKTable);
                diff.setForeignTableDiff(fkTableDiff);
            }
        }
        if (diff.hasDiff()) {
            _tableDiff.addForeignKeyDiff(diff);
        }
    }

    public DfForeignKeyDiff createAddedDiff(String constraintName) {
        return DfForeignKeyDiff.createAdded(constraintName);
    }

    public DfForeignKeyDiff createChangedDiff(String constraintName) {
        return DfForeignKeyDiff.createChanged(constraintName);
    }

    public DfForeignKeyDiff createDeletedDiff(String constraintName) {
        return DfForeignKeyDiff.createDeleted(constraintName);
    }
}