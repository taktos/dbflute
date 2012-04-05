package org.seasar.dbflute.cbean.chelper;

import org.seasar.dbflute.cbean.coption.ColumnConversionOption;

/**
 * @author jflute
 */
public interface HpCalculator {

    // ===================================================================================
    //                                                                         Calculation
    //                                                                         ===========
    /**
     * Plus the specified column with the value. (+)
     * @param plusValue The number value for plus. (NotNull)
     * @return this. (NotNull)
     */
    HpCalculator plus(Number plusValue);

    /**
     * Minus the specified column with the value. (-)
     * @param minusValue The number value for minus. (NotNull)
     * @return this. (NotNull)
     */
    HpCalculator minus(Number minusValue);

    /**
     * Multiply the value to the specified column. (*)
     * @param multiplyValue The number value for multiply. (NotNull)
     * @return this. (NotNull)
     */
    HpCalculator multiply(Number multiplyValue);

    /**
     * Divide the specified column by the value. (/)
     * @param divideValue The number value for divide. (NotNull)
     * @return this. (NotNull)
     */
    HpCalculator divide(Number divideValue);

    /**
     * Convert the value of right column by function.
     * @param option The conversion option of column. (NotNull)
     * @return this. (NotNull)
     */
    HpCalculator convert(ColumnConversionOption option);

    // ===================================================================================
    //                                                                     Left/Right Mode
    //                                                                     ===============
    /**
     * To be for left column.
     * @return this. (NotNull)
     */
    HpCalculator left();

    /**
     * To be for right column. (default)<br />
     * It also means main process internally.
     * @return this. (NotNull)
     */
    HpCalculator right();

    // ===================================================================================
    //                                                                       Related Class
    //                                                                       =============
    public static class CalculationElement {
        protected CalculationType _calculationType;
        protected Number _calculationValue;
        protected ColumnConversionOption _columnConversionOption;
        protected boolean _preparedConvOption;

        protected String buildExp(String targetExp) {
            if (_calculationType.equals(CalculationType.CONV)) {
                return _columnConversionOption.filterFunction(targetExp);
            } else {
                return targetExp + " " + _calculationType.operand() + " " + _calculationValue;
            }
        }

        public CalculationType getCalculationType() {
            return _calculationType;
        }

        public void setCalculationType(CalculationType calculationType) {
            this._calculationType = calculationType;
        }

        public Number getCalculationValue() {
            return _calculationValue;
        }

        public void setCalculationValue(Number calculationValue) {
            this._calculationValue = calculationValue;
        }

        public ColumnConversionOption getColumnConversionOption() {
            return _columnConversionOption;
        }

        public void setColumnConversionOption(ColumnConversionOption columnConversionOption) {
            this._columnConversionOption = columnConversionOption;
        }

        public boolean isPreparedConvOption() {
            return _preparedConvOption;
        }

        public void setPreparedConvOption(boolean preparedConvOption) {
            this._preparedConvOption = preparedConvOption;
        }
    }

    public static enum CalculationType {
        CONV("$$FUNC$$"), PLUS("+"), MINUS("-"), MULTIPLY("*"), DIVIDE("/");
        private String _operand;

        private CalculationType(String operand) {
            _operand = operand;
        }

        public String operand() {
            return _operand;
        }
    }
}
