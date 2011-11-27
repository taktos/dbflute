package org.seasar.dbflute.helper;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.seasar.dbflute.util.DfTypeUtil;

/**
 * Now making...
 * @author jflute
 * @since 0.9.9.2A (2011/11/17 Thursday)
 */
public class HandyDate implements Serializable, Cloneable {

    private static final long serialVersionUID = -5181512291555841795L;

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final Calendar _cal = Calendar.getInstance();
    protected int _yearBeginMonth = _cal.getActualMinimum(Calendar.MONTH) + 1; // as default (zero origin headache)
    protected int _monthBeginDay = _cal.getActualMinimum(Calendar.DAY_OF_MONTH); // as default
    protected int _dayBeginHour = _cal.getActualMinimum(Calendar.HOUR_OF_DAY); // as default
    protected int _weekBeginDay = Calendar.SUNDAY; // as default

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    /**
     * Construct the handy date by the specified date. <br />
     * The specified date is not changed by this handy date.
     * @param date The instance of the date. (NotNull)
     */
    public HandyDate(Date date) {
        assertConstructorArgumentNotNull("date", date);
        _cal.setTime(date);
    }

    /**
     * Construct the handy date by the string expression.
     * <pre>
     * e.g.
     * o new HandyDate("2001/01/01"): 2001-01-01 00:00:00.000
     * o new HandyDate("2001-01-01"): 2001-01-01 00:00:00.000
     * o new HandyDate("2001/01/01 12:34:56"): 2001-01-01 12:34:56.000
     * o new HandyDate("2001/01/01 12:34:56.798"): 2001-01-01 12:34:56.789
     * o new HandyDate("date 20010101"): 2001-01-01
     * </pre>
     * @param exp The string expression of the date. (NotNull)
     */
    public HandyDate(String exp) {
        assertConstructorArgumentNotNull("exp", exp);
        _cal.setTime(DfTypeUtil.toDate(exp));
    }

    /**
     * Construct the handy date by the string expression. <br />
     * e.g. new HandyDate("20010101", "yyyyMMdd"): 2001-01-01 00:00:00.000
     * @param exp The string expression of the date. (NotNull)
     * @param pattern
     */
    public HandyDate(String exp, String pattern) {
        assertConstructorArgumentNotNull("exp", exp);
        assertConstructorArgumentNotNull("pattern", pattern);
        _cal.setTime(DfTypeUtil.toDate(exp, pattern));
    }

    protected void assertConstructorArgumentNotNull(String name, Object value) {
        if (value == null) {
            String msg = "The argument '" + name + "' should not be null.";
            throw new IllegalArgumentException(msg);
        }
    }

    // ===================================================================================
    //                                                                            Add Date
    //                                                                            ========
    /**
     * Add years. e.g. addYear(1): 2001/01/01 to <span style="color: #FD4747">2002</span>/01/01
     * @param year The added count of year. (MinusAllowed: if minus, move back)
     * @return this.
     */
    public HandyDate addYear(int year) {
        DfTypeUtil.addCalendarYear(_cal, year);
        return this;
    }

    /**
     * Add months. e.g. addMonth(1): 2001/01/01 to 2001/<span style="color: #FD4747">02</span>/01
     * @param month The added count of month. (MinusAllowed: if minus, move back)
     * @return this.
     */
    public HandyDate addMonth(int month) {
        DfTypeUtil.addCalendarMonth(_cal, month);
        return this;
    }

    /**
     * Add days. e.g. addDay(1): 2001/01/01 to 2001/01/<span style="color: #FD4747">02<span>
     * @param day The added count of day. (MinusAllowed: if minus, move back)
     * @return this.
     */
    public HandyDate addDay(int day) {
        DfTypeUtil.addCalendarDay(_cal, day);
        return this;
    }

    /**
     * Add hours. e.g. addHour(1): 2001/01/01 00:00:00 to 2001/01/02 <span style="color: #FD4747">01</span>:00:00
     * @param hour The added count of hour. (MinusAllowed: if minus, move back)
     * @return this.
     */
    public HandyDate addHour(int hour) {
        DfTypeUtil.addCalendarHour(_cal, hour);
        return this;
    }

    /**
     * Add minutes. e.g. addMinute(1): 2001/01/01 00:00:00 to 2001/01/02 00:<span style="color: #FD4747">01</span>:00
     * @param minute The added count of minute. (MinusAllowed: if minus, move back)
     * @return this.
     */
    public HandyDate addMinute(int minute) {
        DfTypeUtil.addCalendarMinute(_cal, minute);
        return this;
    }

    /**
     * Add seconds. e.g. addSecond(1): 2001/01/01 00:00:00 to 2001/01/02 00:00:<span style="color: #FD4747">01</span>
     * @param second The added count of second. (MinusAllowed: if minus, move back)
     * @return this.
     */
    public HandyDate addSecond(int second) {
        DfTypeUtil.addCalendarSecond(_cal, second);
        return this;
    }

    /**
     * Add milliseconds. e.g. addMillisecond(1): 2001/01/01 00:00:00.000 to 2001/01/02 00:00:00.<span style="color: #FD4747">001</span>
     * @param millisecond The added count of millisecond. (MinusAllowed: if minus, move back)
     * @return this.
     */
    public HandyDate addMillisecond(int millisecond) {
        DfTypeUtil.addCalendarMillisecond(_cal, millisecond);
        return this;
    }

    /**
     * Add weeks. e.g. addWeek(1): 2001/01/01 to 2001/01/<span style="color: #FD4747">08</span>
     * @param week The added count of week. (MinusAllowed: if minus, move back)
     * @return this.
     */
    public HandyDate addWeek(int week) {
        DfTypeUtil.addCalendarWeek(_cal, week);
        return this;
    }

    // ===================================================================================
    //                                                                        Move-to Date
    //                                                                        ============
    // -----------------------------------------------------
    //                                          Move-to Year
    //                                          ------------
    /**
     * Move to the specified year.
     * <pre>
     * e.g.
     * o moveToYear(2007): 2001/01/01 to <span style="color: #FD4747">2007</span>/01/01
     * o moveToYear(-2007): 2001/01/01 to <span style="color: #FD4747">BC2007</span>/01/01
     * </pre>
     * @param year The move-to year. (NotZero, MinusAllowed: if minus, means before Christ)
     * @return this.
     */
    public HandyDate moveToYear(int year) {
        DfTypeUtil.moveToCalendarYear(_cal, year);
        return this;
    }

    /**
     * Move to the year just. <br />
     * e.g. moveToYearJust(): 2011/11/27 12:34:56.789 to 2011/<span style="color: #FD4747">01/01 00:00:00.000</span>
     * @return this.
     */
    public HandyDate moveToYearJust() {
        DfTypeUtil.moveToCalendarYearJust(_cal, _yearBeginMonth);
        moveToMonthJust(); // just for others
        return this;
    }

    /**
     * Move to the year just after the year added. <br />
     * e.g. moveToYearJustAdded(1): 2011/11/27 12:34:56.789 to 2012/01/01 00:00:00.000
     * @param year The count added of year. (MinusAllowed: if minus, move back)
     * @return this.
     */
    public HandyDate moveToYearJustAdded(int year) {
        DfTypeUtil.moveToCalendarYearJustAdded(_cal, year);
        return this;
    }

    /**
     * Move to the year just after the year moved-to. <br />
     * e.g. moveToYearJustFor(2007): 2011/11/27 12:34:56.789 to 2007/01/01 00:00:00.000
     * @param year The move-to year. (NotZero, MinusAllowed: if minus, means before Christ)
     * @return this.
     */
    public HandyDate moveToYearJustFor(int year) {
        DfTypeUtil.moveToCalendarYearJustFor(_cal, year);
        return this;
    }

    /**
     * Move to the terminal of the year. <br />
     * e.g. moveToYearTerminal(): 2011/11/27 12:34:56.789 to 2011/<span style="color: #FD4747">12/31 23:59:59.999</span>
     * @return this.
     */
    public HandyDate moveToYearTerminal() {
        DfTypeUtil.moveToCalendarYearTerminal(_cal, _yearBeginMonth);
        moveToMonthTerminal(); // just for others
        return this;
    }

    /**
     * Move to the terminal of the year after the year added. <br />
     * e.g. moveToYearTerminalAdded(1): 2011/11/27 12:34:56.789 to 2012/12/31 23:59:59.999
     * @param year The count added of year. (MinusAllowed: if minus, move back)
     * @return this.
     */
    public HandyDate moveToYearTerminalAdded(int year) {
        DfTypeUtil.moveToCalendarYearTerminalAdded(_cal, year);
        return this;
    }

    /**
     * Move to the terminal of the year after the year moved-to. <br />
     * e.g. moveToYearTerminalFor(2007): 2011/11/27 12:34:56.789 to 2007/12/31 23:59:59.999
     * @param year The move-to year. (NotZero, MinusAllowed: if minus, means before Christ)
     * @return this.
     */
    public HandyDate moveToYearTerminalFor(int year) {
        DfTypeUtil.moveToCalendarYearTerminalFor(_cal, year);
        return this;
    }

    // -----------------------------------------------------
    //                                         Move-to Month
    //                                         -------------
    /**
     * Move to the specified month. <br />
     * e.g. moveToMonth(9): 2011/11/27 to 2011/<span style="color: #FD4747">09</span>/27
     * @param month The move-to month. (NotZero, NotMinus)
     * @return this.
     */
    public HandyDate moveToMonth(int month) {
        assertValidMonth(month);
        DfTypeUtil.moveToCalendarMonth(_cal, month);
        return this;
    }

    /**
     * Move to the month just. <br />
     * e.g. moveToMonthJust(): 2011/11/27 12:34:56.789 to 2011/11/<span style="color: #FD4747">01 00:00:00.000</span>
     * @return this.
     */
    public HandyDate moveToMonthJust() {
        DfTypeUtil.moveToCalendarMonthJust(_cal, _monthBeginDay);
        moveToDayJust(); // just for others
        return this;
    }

    /**
     * Move to the month just after the month added. <br />
     * e.g. moveToMonthJustAdded(1): 2011/11/27 12:34:56.789 to 2011/12/01 00:00:00.000
     * @param month The count added of month. (MinusAllowed: if minus, move back)
     * @return this.
     */
    public HandyDate moveToMonthJustAdded(int month) {
        DfTypeUtil.moveToCalendarMonthJustAdded(_cal, month);
        return this;
    }

    /**
     * Move to the month just after the month moved-to. <br />
     * e.g. moveToMonthJustFor(9): 2011/11/27 12:34:56.789 to 2011/09/01 00:00:00.000
     * @param month The move-to month. (NotZero, NotMinus)
     * @return this.
     */
    public HandyDate moveToMonthJustFor(int month) {
        assertValidMonth(month);
        DfTypeUtil.moveToCalendarMonthJustFor(_cal, month);
        return this;
    }

    /**
     * Move to the terminal of the month. <br />
     * e.g. moveToMonthTerminal(): 2011/11/27 12:34:56.789 to 2011/11/<span style="color: #FD4747">30 23:59:59.999</span>
     * @return this.
     */
    public HandyDate moveToMonthTerminal() {
        DfTypeUtil.moveToCalendarMonthTerminal(_cal, _monthBeginDay);
        moveToDayTerminal(); // just for others
        return this;
    }

    /**
     * Move to the terminal of the month after the month added. <br />
     * e.g. moveToMonthTerminalAdded(1): 2011/11/27 12:34:56.789 to 2011/12/31 23:59:59.999
     * @param month The count added of month. (MinusAllowed: if minus, move back)
     * @return this.
     */
    public HandyDate moveToMonthTerminalAdded(int month) {
        DfTypeUtil.moveToCalendarMonthTerminalAdded(_cal, month);
        return this;
    }

    /**
     * Move to the terminal of the month after the month moved-to. <br />
     * e.g. moveToMonthTerminalFor(9): 2011/11/27 12:34:56.789 to 2011/09/30 23:59:59.999
     * @param month The move-to month. (NotZero, NotMinus)
     * @return this.
     */
    public HandyDate moveToMonthTerminalFor(int month) {
        assertValidMonth(month);
        DfTypeUtil.moveToCalendarMonthTerminalFor(_cal, month);
        return this;
    }

    // -----------------------------------------------------
    //                                           Move-to Day
    //                                           -----------
    /**
     * Move to the specified day. <br />
     * e.g. moveToDay(23): 2001/01/16 to 2007/01/<span style="color: #FD4747">23</span>
     * @param day The move-to day. (NotZero, NotMinus)
     * @return this.
     */
    public HandyDate moveToDay(int day) {
        assertValidDay(day);
        DfTypeUtil.moveToCalendarDay(_cal, day);
        return this;
    }

    /**
     * Move to the day just. <br />
     * e.g. moveToDayJust(): 2011/11/27 12:34:56.789 to 2011/11/27 <span style="color: #FD4747">00:00:00.000</span>
     * @return this.
     */
    public HandyDate moveToDayJust() {
        DfTypeUtil.moveToCalendarDayJust(_cal, _dayBeginHour);
        return this;
    }

    /**
     * Move to the day just after the day added. <br />
     * e.g. moveToDayJustAdded(1): 2011/11/27 12:34:56.789 to 2011/11/28 00:00:00.000
     * @param day The count added of day. (MinusAllowed: if minus, move back)
     * @return this.
     */
    public HandyDate moveToDayJustAdded(int day) {
        DfTypeUtil.moveToCalendarDayJustAdded(_cal, day);
        return this;
    }

    /**
     * Move to the day just after the day moved-to. <br />
     * e.g. moveToDayJustFor(14): 2011/11/27 12:34:56.789 to 2011/11/14 00:00:00.000
     * @param day The move-to day. (NotZero, NotMinus)
     * @return this.
     */
    public HandyDate moveToDayJustFor(int day) {
        assertValidDay(day);
        DfTypeUtil.moveToCalendarDayJustFor(_cal, day);
        return this;
    }

    /**
     * Move to the terminal of the day. <br />
     * e.g. moveToDayTerminal(): 2011/11/27 12:34:56.789 to 2011/11/27 <span style="color: #FD4747">23:59:59.999</span>
     * @return this.
     */
    public HandyDate moveToDayTerminal() {
        DfTypeUtil.moveToCalendarDayTerminal(_cal, _dayBeginHour);
        return this;
    }

    /**
     * Move to the terminal of the day after the day added. <br />
     * e.g. moveToDayJustAdded(1): 2011/11/27 12:34:56.789 to 2011/11/28 23:59:59.999
     * @param day The count added of day. (MinusAllowed: if minus, move back)
     * @return this.
     */
    public HandyDate moveToDayTerminalAdded(int day) {
        DfTypeUtil.moveToCalendarDayTerminalAdded(_cal, day);
        return this;
    }

    /**
     * Move to the day just after the day moved-to. <br />
     * e.g. moveToDayTerminalFor(14): 2011/11/27 12:34:56.789 to 2011/11/14 23:59:59.999
     * @param day The move-to day. (NotZero, NotMinus)
     * @return this.
     */
    public HandyDate moveToDayTerminalFor(int day) {
        assertValidDay(day);
        DfTypeUtil.moveToCalendarDayTerminalFor(_cal, day);
        return this;
    }

    // -----------------------------------------------------
    //                                          Move-to Hour
    //                                          ------------
    /**
     * Move to the specified hour.
     * <pre>
     * e.g. moveToHour(23): 2011/11/27 00:00:00 to 2007/11/27 <span style="color: #FD4747">23</span>:00:00
     * e.g. moveToHour(26): 2011/11/27 00:00:00 to 2007/11/<span style="color: #FD4747">28 02</span>:00:00
     * </pre>
     * @param hour The move-to hour. (NotZero, MinusAllowed)
     * @return this.
     */
    public HandyDate moveToHour(int hour) {
        assertValidHour(hour);
        DfTypeUtil.moveToCalendarHour(_cal, hour);
        return this;
    }

    /**
     * Move to the hour just. <br />
     * e.g. moveToHourJust(): 2011/11/27 12:34:56.789 to 2011/11/27 12:<span style="color: #FD4747">00:00.000</span>
     * @return this.
     */
    public HandyDate moveToHourJust() {
        DfTypeUtil.moveToCalendarHourJust(_cal);
        return this;
    }

    public HandyDate moveToHourJustAdded(int hour) {
        DfTypeUtil.moveToCalendarHourJustAdded(_cal, hour);
        return this;
    }

    public HandyDate moveToHourJustFor(int hour) {
        assertValidHour(hour);
        DfTypeUtil.moveToCalendarHourJustFor(_cal, hour);
        return this;
    }

    public HandyDate moveToHourTerminal() {
        DfTypeUtil.moveToCalendarHourTerminal(_cal);
        return this;
    }

    public HandyDate moveToHourTerminalAdded(int hour) {
        DfTypeUtil.moveToCalendarHourTerminalAdded(_cal, hour);
        return this;
    }

    public HandyDate moveToHourTerminalFor(int hour) {
        assertValidHour(hour);
        DfTypeUtil.moveToCalendarHourTerminalFor(_cal, hour);
        return this;
    }

    public HandyDate moveToHourJustNoon() {
        DfTypeUtil.moveToCalendarHourJustNoon(_cal);
        return this;
    }

    // -----------------------------------------------------
    //                                        Move-to Minute
    //                                        --------------
    public HandyDate moveToMinute(int minute) {
        assertValidMinute(minute);
        DfTypeUtil.moveToCalendarMinute(_cal, minute);
        return this;
    }

    /**
     * Move to the minute just. <br />
     * e.g. moveToMinuteJust(): 2011/11/27 12:34:56.789 to 2011/11/27 12:34:<span style="color: #FD4747">00.000</span>
     * @return this.
     */
    public HandyDate moveToMinuteJust() {
        DfTypeUtil.moveToCalendarMinuteJust(_cal);
        return this;
    }

    public HandyDate moveToMinuteJustAdded(int minute) {
        DfTypeUtil.moveToCalendarMinuteJustAdded(_cal, minute);
        return this;
    }

    public HandyDate moveToMinuteJustFor(int minute) {
        assertValidMinute(minute);
        DfTypeUtil.moveToCalendarMinuteJustFor(_cal, minute);
        return this;
    }

    public HandyDate moveToMinuteTerminal() {
        DfTypeUtil.moveToCalendarMinuteTerminal(_cal);
        return this;
    }

    public HandyDate moveToMinuteTerminalÅdded(int minute) {
        DfTypeUtil.moveToCalendarMinuteTerminalAdded(_cal, minute);
        return this;
    }

    public HandyDate moveToMinuteTerminalFor(int minute) {
        assertValidMinute(minute);
        DfTypeUtil.moveToCalendarMinuteTerminalFor(_cal, minute);
        return this;
    }

    // -----------------------------------------------------
    //                                        Move-to Second
    //                                        --------------
    public HandyDate moveToSecond(int second) {
        assertValidSecond(second);
        DfTypeUtil.moveToCalendarSecond(_cal, second);
        return this;
    }

    /**
     * Move to the second just. <br />
     * e.g. moveToSecondJust(): 2011/11/27 12:34:56.789 to 2011/11/27 12:34:56.<span style="color: #FD4747">000</span>
     * @return this.
     */
    public HandyDate moveToSecondJust() {
        DfTypeUtil.moveToCalendarSecondJust(_cal);
        return this;
    }

    public HandyDate moveToSecondJustFor(int second) {
        assertValidSecond(second);
        DfTypeUtil.moveToCalendarSecondJustFor(_cal, second);
        return this;
    }

    public HandyDate moveToSecondJustAdded(int second) {
        DfTypeUtil.moveToCalendarSecondJustAdded(_cal, second);
        return this;
    }

    public HandyDate moveToSecondTerminal() {
        DfTypeUtil.moveToCalendarSecondTerminal(_cal);
        return this;
    }

    public HandyDate moveToSecondTerminalAdded(int second) {
        DfTypeUtil.moveToCalendarSecondTerminalAdded(_cal, second);
        return this;
    }

    public HandyDate moveToSecondTerminalFor(int second) {
        assertValidSecond(second);
        DfTypeUtil.moveToCalendarSecondTerminalFor(_cal, second);
        return this;
    }

    // -----------------------------------------------------
    //                                   Move-to Millisecond
    //                                   -------------------
    public HandyDate moveToMillisecond(int millisecond) {
        assertValidMillisecond(millisecond);
        DfTypeUtil.moveToCalendarMillisecond(_cal, millisecond);
        return this;
    }

    // -----------------------------------------------------
    //                                          Move-to Week
    //                                          ------------
    public HandyDate moveToWeekOfMonth(int weekOfMonth) {
        DfTypeUtil.moveToCalendarWeekOfMonth(_cal, weekOfMonth);
        return this;
    }

    public HandyDate moveToWeekOfYear(int weekOfYear) {
        DfTypeUtil.moveToCalendarWeekOfYear(_cal, weekOfYear);
        return this;
    }

    public HandyDate moveToWeekJust() {
        DfTypeUtil.moveToCalendarWeekJust(_cal, _weekBeginDay);
        return this;
    }

    public HandyDate moveToWeekTerminal() {
        DfTypeUtil.moveToCalendarWeekTerminal(_cal, _weekBeginDay);
        return this;
    }

    // -----------------------------------------------------
    //                               Move-to Quarter of Year
    //                               -----------------------
    public HandyDate moveToQuarterOfYearJust() {
        DfTypeUtil.moveToCalendarQuarterOfYearJust(_cal, _yearBeginMonth);
        moveToMonthJust(); // just for others
        return this;
    }

    public HandyDate moveToQuarterOfYearJustAdded(int quarterOfYear) {
        DfTypeUtil.moveToCalendarQuarterOfYearJustAdded(_cal, quarterOfYear, _yearBeginMonth);
        moveToMonthJust(); // just for others
        return this;
    }

    public HandyDate moveToQuarterOfYearJustFor(int quarterOfYear) {
        DfTypeUtil.moveToCalendarQuarterOfYearJustFor(_cal, quarterOfYear, _yearBeginMonth);
        moveToMonthJust(); // just for others
        return this;
    }

    public HandyDate moveToQuarterOfYearTerminal() {
        DfTypeUtil.moveToCalendarQuarterOfYearTerminal(_cal, _yearBeginMonth);
        moveToMonthTerminal(); // just for others
        return this;
    }

    public HandyDate moveToQuarterOfYearTerminalAdded(int quarterOfYear) {
        DfTypeUtil.moveToCalendarQuarterOfYearTerminalAdded(_cal, quarterOfYear, _yearBeginMonth);
        moveToMonthTerminal(); // just for others
        return this;
    }

    public HandyDate moveToQuarterOfYearTerminalFor(int quarterOfYear) {
        DfTypeUtil.moveToCalendarQuarterOfYearTerminalFor(_cal, quarterOfYear, _yearBeginMonth);
        moveToMonthTerminal(); // just for others
        return this;
    }

    // ===================================================================================
    //                                                                          Clear Date
    //                                                                          ==========
    /**
     * Clear the time parts, hours, minutes, seconds, milliseconds. <br />
     * e.g. clearTimeParts(): 2011/11/27 12:34:56.789 to 2011/11/27 00:00:00.000
     * @return this.
     */
    public HandyDate clearTimeParts() {
        DfTypeUtil.clearCalendarTimeParts(_cal);
        return this;
    }

    /**
     * Clear the minute with rear parts, minutes, seconds, milliseconds. <br />
     * e.g. clearMinuteWithRear(): 2011/11/27 12:34:56.789 to 2011/11/27 12:00:00.000
     * @return this.
     */
    public HandyDate clearMinuteWithRear() {
        DfTypeUtil.clearCalendarMinuteWithRear(_cal);
        return this;
    }

    /**
     * Clear the second with rear parts, seconds, milliseconds. <br />
     * e.g. clearSecondWithRear(): 2011/11/27 12:34:56.789 to 2011/11/27 12:34:00.000
     * @return this.
     */
    public HandyDate clearSecondWithRear() {
        DfTypeUtil.clearCalendarSecondWithRear(_cal);
        return this;
    }

    /**
     * Clear the millisecond. <br />
     * e.g. clearMillisecond(): 2011/11/27 12:34:56.789 to 2011/11/27 12:34:56.000
     * @return this.
     */
    public HandyDate clearMillisecond() {
        DfTypeUtil.clearCalendarMillisecond(_cal);
        return this;
    }

    // ===================================================================================
    //                                                                        Confirm Date
    //                                                                        ============
    // -----------------------------------------------------
    //                                          Confirm Year
    //                                          ------------
    public boolean isYear(int year) {
        return getYear() == year;
    }

    public boolean isYear_AnnoDomini() {
        return getYear() > 0;
    }

    public boolean isYear_BeforeChrist() {
        return getYear() < 0;
    }

    // -----------------------------------------------------
    //                                         Confirm Month
    //                                         -------------
    public boolean isMonth(int month) {
        return getMonth() == month; // zero origin headache
    }

    public boolean isMonth01_January() {
        return isMonth(1);
    }

    public boolean isMonth02_February() {
        return isMonth(2);
    }

    public boolean isMonth03_March() {
        return isMonth(3);
    }

    public boolean isMonth04_April() {
        return isMonth(4);
    }

    public boolean isMonth05_May() {
        return isMonth(5);
    }

    public boolean isMonth06_June() {
        return isMonth(6);
    }

    public boolean isMonth07_July() {
        return isMonth(7);
    }

    public boolean isMonth08_August() {
        return isMonth(8);
    }

    public boolean isMonth09_September() {
        return isMonth(9);
    }

    public boolean isMonth10_October() {
        return isMonth(10);
    }

    public boolean isMonth11_November() {
        return isMonth(11);
    }

    public boolean isMonth12_December() {
        return isMonth(12);
    }

    // -----------------------------------------------------
    //                                           Confirm Day
    //                                           -----------
    public boolean isDay(int day) {
        return getDay() == day;
    }

    public boolean isDay_MonthFirstDay() {
        return isDay(_cal.getActualMinimum(Calendar.DAY_OF_MONTH));
    }

    public boolean isDay_MonthLastDay() {
        return isDay(_cal.getActualMaximum(Calendar.DAY_OF_MONTH));
    }

    // ===================================================================================
    //                                                                          Begin Date
    //                                                                          ==========
    // -----------------------------------------------------
    //                                            Begin Year
    //                                            ----------
    public HandyDate beginYear_Month(Date yearBeginMonth) {
        assertArgumentNotNull("yearBeginMonth", yearBeginMonth);
        final Calendar cal = Calendar.getInstance();
        cal.setTime(yearBeginMonth);
        _yearBeginMonth = cal.get(Calendar.MONTH) + 1; // zero origin headache
        return this;
    }

    public HandyDate beginYear_Month(int yearBeginMonth) {
        assertNotMinusNotOver("yearBeginMonth", yearBeginMonth, 12);
        _yearBeginMonth = yearBeginMonth;
        return this;
    }

    public HandyDate beginYear_Month01_January() {
        _yearBeginMonth = 1;
        return this;
    }

    public HandyDate beginYear_Month02_February() {
        _yearBeginMonth = 2;
        return this;
    }

    public HandyDate beginYear_Month03_March() {
        _yearBeginMonth = 3;
        return this;
    }

    public HandyDate beginYear_Month04_April() {
        _yearBeginMonth = 4;
        return this;
    }

    public HandyDate beginYear_Month05_May() {
        _yearBeginMonth = 5;
        return this;
    }

    public HandyDate beginYear_Month06_June() {
        _yearBeginMonth = 6;
        return this;
    }

    public HandyDate beginYear_Month07_July() {
        _yearBeginMonth = 7;
        return this;
    }

    public HandyDate beginYear_Month08_August() {
        _yearBeginMonth = 8;
        return this;
    }

    public HandyDate beginYear_Month09_September() {
        _yearBeginMonth = 9;
        return this;
    }

    public HandyDate beginYear_Month10_October() {
        _yearBeginMonth = 10;
        return this;
    }

    public HandyDate beginYear_Month11_November() {
        _yearBeginMonth = 11;
        return this;
    }

    public HandyDate beginYear_Month12_December() {
        _yearBeginMonth = 12;
        return this;
    }

    public HandyDate beginYear_PreviousMonth(int yearBeginMonth) {
        assertNotMinusNotOver("yearBeginMonth", yearBeginMonth, 12);
        _yearBeginMonth = -yearBeginMonth; // to be minus
        return this;
    }

    // -----------------------------------------------------
    //                                           Begin Month
    //                                           -----------
    public HandyDate beginMonth_Day(Date monthBeginDay) {
        assertArgumentNotNull("monthBeginDay", monthBeginDay);
        final Calendar cal = Calendar.getInstance();
        cal.setTime(monthBeginDay);
        _monthBeginDay = cal.get(Calendar.DAY_OF_MONTH);
        return this;
    }

    public HandyDate beginMonth_Day(int monthBeginDay) {
        assertNotMinusNotOver("monthBeginDay", monthBeginDay, 31);
        _monthBeginDay = monthBeginDay;
        return this;
    }

    public HandyDate beginMonth_PreviousDay(int monthBeginDay) {
        assertNotMinusNotOver("monthBeginDay", monthBeginDay, 31);
        _monthBeginDay = -monthBeginDay; // to be minus
        return this;
    }

    // -----------------------------------------------------
    //                                             Begin Day
    //                                             ---------
    public HandyDate beginDay_Hour(Date dayBeginHour) {
        assertArgumentNotNull("dayBeginHour", dayBeginHour);
        final Calendar cal = Calendar.getInstance();
        cal.setTime(dayBeginHour);
        _dayBeginHour = cal.get(Calendar.HOUR_OF_DAY);
        return this;
    }

    public HandyDate beginDay_Hour(int dayBeginHour) {
        assertNotMinusNotOver("dayBeginHour", dayBeginHour, 23);
        _dayBeginHour = dayBeginHour;
        return this;
    }

    public HandyDate beginDay_PreviousHour(int dayBeginHour) {
        assertNotMinusNotOver("dayBeginHour", dayBeginHour, 23);
        _dayBeginHour = -dayBeginHour; // to be minus
        return this;
    }

    // -----------------------------------------------------
    //                                            Begin Week
    //                                            ----------
    public HandyDate beginWeek_DayOfWeek(Date weekBeginDayOfWeek) {
        assertArgumentNotNull("weekBeginDayOfWeek", weekBeginDayOfWeek);
        final Calendar cal = Calendar.getInstance();
        cal.setTime(weekBeginDayOfWeek);
        _weekBeginDay = cal.get(Calendar.DAY_OF_WEEK);
        return this;
    }

    public HandyDate beginWeek_DayOfWeek1st_Sunday() {
        _weekBeginDay = Calendar.SUNDAY;
        return this;
    }

    public HandyDate beginWeek_DayOfWeek2nd_Monday() {
        _weekBeginDay = Calendar.MONDAY;
        return this;
    }

    public HandyDate beginWeek_DayOfWeek3rd_Tuesday() {
        _weekBeginDay = Calendar.TUESDAY;
        return this;
    }

    public HandyDate beginWeek_DayOfWeek4th_Wednesday() {
        _weekBeginDay = Calendar.WEDNESDAY;
        return this;
    }

    public HandyDate beginWeek_DayOfWeek5th_Thursday() {
        _weekBeginDay = Calendar.THURSDAY;
        return this;
    }

    public HandyDate beginWeek_DayOfWeek6th_Friday() {
        _weekBeginDay = Calendar.FRIDAY;
        return this;
    }

    public HandyDate beginWeek_DayOfWeek7th_Saturday() {
        _weekBeginDay = Calendar.SATURDAY;
        return this;
    }

    // ===================================================================================
    //                                                                            Get Date
    //                                                                            ========
    /**
     * Get created new date that has the same time of this handy date. 
     * @return The instance of date. (NotNull)
     */
    public Date getDate() {
        return new Date(_cal.getTimeInMillis());
    }

    /**
     * Get created new time-stamp that has the same time of this handy date. 
     * @return The instance of time-stamp. (NotNull)
     */
    public Timestamp getTimestamp() {
        return new Timestamp(_cal.getTimeInMillis());
    }

    // ===================================================================================
    //                                                                           Get Parts
    //                                                                           =========
    protected int getYear() {
        final int year = _cal.get(Calendar.YEAR);
        final int era = _cal.get(Calendar.ERA);
        return era == GregorianCalendar.AD ? year : -year;
    }

    protected int getMonth() {
        return _cal.get(Calendar.MONTH) + 1; // zero origin headache
    }

    protected int getDay() {
        return _cal.get(Calendar.DAY_OF_MONTH);
    }

    protected int getFirstDayOfMonth() {
        return _cal.getActualMinimum(Calendar.DAY_OF_MONTH);
    }

    protected int getLastDayOfMonth() {
        return _cal.getActualMaximum(Calendar.DAY_OF_MONTH);
    }

    // ===================================================================================
    //                                                                       Assert Helper
    //                                                                       =============
    protected void assertArgumentNotNull(String name, Object value) {
        if (value == null) {
            String msg = "The argument '" + name + "' should not be null.";
            throw new IllegalArgumentException(msg);
        }
    }

    protected void assertNotMinusNotOver(String name, int value, int max) {
        if (value < 0) {
            String msg = "The argument '" + name + "' should not be minus: value=" + value;
            throw new IllegalArgumentException(msg);
        }
        if (value > max) {
            String msg = "The argument '" + name + "' should not be over: value=" + value + " max=" + max;
            throw new IllegalArgumentException(msg);
        }
    }

    protected void assertValidMonth(int month) {
        if (month < 1 || month > 12) {
            String msg = "The argument 'month' should be 1 to 12: " + month;
            throw new IllegalArgumentException(msg);
        }
    }

    protected void assertValidDay(int day) {
        final int firstDayOfMonth = getFirstDayOfMonth();
        final int lastDayOfMonth = getLastDayOfMonth();
        if (day < firstDayOfMonth || day > lastDayOfMonth) {
            String msg = "The argument 'day' should be " + firstDayOfMonth + " to " + lastDayOfMonth + ": " + day;
            throw new IllegalArgumentException(msg);
        }
    }

    protected void assertValidHour(int hour) {
        // e.g. 26h allowed
        //if (hour < 1 || hour > 12) {
        //    String msg = "The argument 'hour' should be 0 to 23: " + hour;
        //    throw new IllegalArgumentException(msg);
        //}
    }

    protected void assertValidMinute(int minute) {
        if (minute < 0 || minute > 59) {
            String msg = "The argument 'minute' should be 0 to 59: " + minute;
            throw new IllegalArgumentException(msg);
        }
    }

    protected void assertValidSecond(int second) {
        if (second < 0 || second > 59) {
            String msg = "The argument 'second' should be 0 to 59: " + second;
            throw new IllegalArgumentException(msg);
        }
    }

    protected void assertValidMillisecond(int millisecond) {
        if (millisecond < 0 || millisecond > 999) {
            String msg = "The argument 'millisecond' should be 0 to 999: " + millisecond;
            throw new IllegalArgumentException(msg);
        }
    }

    // ===================================================================================
    //                                                                          To Display
    //                                                                          ==========
    public String toDisp(String pattern) {
        return DfTypeUtil.toString(_cal.getTime(), pattern);
    }

    // ===================================================================================
    //                                                                      Basic Override
    //                                                                      ==============
    /**
     * Clone date instance using super.clone(). 
     * @return The cloned instance of this date. (NotNull)
     */
    public HandyDate clone() {
        try {
            return (HandyDate) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new IllegalStateException("Failed to clone the handy date: " + toString(), e);
        }
    }

    @Override
    public int hashCode() {
        final String pattern = getBasicPattern();
        return pattern.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof HandyDate) {
            final HandyDate date = (HandyDate) obj;
            final String pattern = getBasicPattern();
            return date.toDisp(pattern).equals(toDisp(pattern));
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return DfTypeUtil.toString(_cal.getTime(), getBasicPattern());
    }

    protected String getBasicPattern() {
        if (isYear_BeforeChrist()) {
            return "'BC'yyyy/MM/dd HH:mm:ss.SSS";
        } else {
            return "yyyy/MM/dd HH:mm:ss.SSS";
        }
    }
}
