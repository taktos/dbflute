package org.seasar.dbflute.util;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.seasar.dbflute.unit.PlainTestCase;
import org.seasar.dbflute.util.DfTypeUtil.ToDateOutOfCalendarException;
import org.seasar.dbflute.util.DfTypeUtil.ToDateParseException;

/**
 * @author jflute
 * @since 0.9.0 (2009/01/19 Monday)
 */
public class DfTypeUtilTest extends PlainTestCase {

    // ===================================================================================
    //                                                                          Convert To
    //                                                                          ==========
    // -----------------------------------------------------
    //                                               Boolean
    //                                               -------
    public void test_toBoolean() {
        // ## Arrange & Act & Assert ##
        assertNull(DfTypeUtil.toBoolean(null));
        assertTrue(DfTypeUtil.toBoolean("true"));
        assertFalse(DfTypeUtil.toBoolean("false"));
    }

    // -----------------------------------------------------
    //                                                  Date
    //                                                  ----
    public void test_toDate_sameClass() {
        // ## Arrange ##
        SimpleDateFormat f = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date pureDate = new Date(DfTypeUtil.toDate("2009-12-13 12:34:56.123").getTime());

        // ## Act ##
        Date date = DfTypeUtil.toDate(pureDate);

        // ## Assert ##
        assertEquals(java.util.Date.class, date.getClass());
        assertFalse(date instanceof Timestamp); // because it returns pure date
        assertEquals("2009/12/13 12:34:56", f.format(date));
    }

    public void test_toDate_subClass() {
        // ## Arrange ##
        SimpleDateFormat f = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Timestamp timestamp = Timestamp.valueOf("2009-12-13 12:34:56.123");

        // ## Act ##
        Date date = DfTypeUtil.toDate(timestamp);

        // ## Assert ##
        assertEquals(java.util.Date.class, date.getClass());
        assertFalse(date instanceof Timestamp); // because it returns pure date
        assertEquals("2009/12/13 12:34:56", f.format(date));
    }

    public void test_toDate_various() {
        // ## Arrange ##
        SimpleDateFormat f = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        SimpleDateFormat ft = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");

        // ## Act & Assert ##
        assertNull(DfTypeUtil.toDate(null));
        assertNull(DfTypeUtil.toDate(""));
        assertEquals("0002/01/12 00:00:00", f.format(DfTypeUtil.toDate("20112")));
        assertEquals("0012/01/22 00:00:00", f.format(DfTypeUtil.toDate("120122")));
        assertEquals("0923/01/27 00:00:00", f.format(DfTypeUtil.toDate("9230127")));
        assertEquals("2008/12/30 00:00:00", f.format(DfTypeUtil.toDate("20081230")));
        assertEquals("2008/12/30 00:00:00", f.format(DfTypeUtil.toDate("2008/12/30")));
        assertEquals("2008/12/30 00:00:00", f.format(DfTypeUtil.toDate("2008-12-30")));
        assertEquals("2008/12/30 12:34:56", f.format(DfTypeUtil.toDate("2008-12-30 12:34:56")));
        assertEquals("2008/12/30 12:34:56", f.format(DfTypeUtil.toDate("2008-12-30 12:34:56.789")));
        assertEquals("2008/09/30 12:34:56", f.format(DfTypeUtil.toDate("2008-09-30 12:34:56")));
        assertEquals("2008/09/30 12:34:56", f.format(DfTypeUtil.toDate("2008-9-30 12:34:56")));
        assertEquals("2008/09/01 12:34:56", f.format(DfTypeUtil.toDate("2008-9-1 12:34:56")));
        assertEquals("0008/09/01 12:34:56", f.format(DfTypeUtil.toDate("8-9-1 12:34:56")));
        assertEquals("2008/09/01 00:00:00", f.format(DfTypeUtil.toDate("2008-9-1")));
        assertEquals("0008/09/01 02:04:06", f.format(DfTypeUtil.toDate("8-9-1 02:04:06")));
        assertEquals("0008/09/01 02:04:06", f.format(DfTypeUtil.toDate("8-9-1 2:4:6")));
        assertEquals("2008/12/30 12:34:56.000", ft.format(DfTypeUtil.toDate("2008-12-30 12:34:56.789")));
        assertEquals(java.util.Date.class, DfTypeUtil.toDate("2008-12-30 12:34:56.789").getClass());
        assertNotSame(java.sql.Date.class, DfTypeUtil.toDate("2008-12-30 12:34:56.789").getClass());
        assertNotSame(java.sql.Timestamp.class, DfTypeUtil.toDate("2008-12-30 12:34:56.789").getClass());
    }

    public void test_toDate_illegal() {
        try {
            DfTypeUtil.toDate("2009-12");

            fail();
        } catch (ToDateParseException e) {
            // OK
            log(e.getMessage());
        }
        try {
            DfTypeUtil.toDate("2009");

            fail();
        } catch (ToDateParseException e) {
            // OK
            log(e.getMessage());
        }
        try {
            DfTypeUtil.toDate("20091");

            fail();
        } catch (ToDateOutOfCalendarException e) {
            // OK
            log(e.getMessage());
        }
        try {
            DfTypeUtil.toDate("2009-12-09 12:34:60");

            fail();
        } catch (ToDateOutOfCalendarException e) {
            // OK
            log(e.getMessage());
        }
        try {
            DfTypeUtil.toDate("-20091221");

            fail();
        } catch (ToDateParseException e) {
            // OK
            log(e.getMessage());
        }
    }

    public void test_clearSeconds() {
        SimpleDateFormat f = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
        Date date = DfTypeUtil.toDate("2008-12-30 12:34:56.789");

        // ## Act ##
        DfTypeUtil.clearSeconds(date);

        // ## Assert ##
        assertEquals("2008/12/30 00:00:00.000", f.format(date));
    }

    // -----------------------------------------------------
    //                                             Timestamp
    //                                             ---------
    public void test_toTimestamp_various() {
        // ## Arrange ##
        SimpleDateFormat f = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");

        // ## Act & Assert ##
        assertNull(DfTypeUtil.toTimestamp(null));
        assertNull(DfTypeUtil.toTimestamp(""));
        assertEquals("0002/01/12 00:00:00.000", f.format(DfTypeUtil.toTimestamp("20112")));
        assertEquals("0012/01/22 00:00:00.000", f.format(DfTypeUtil.toTimestamp("120122")));
        assertEquals("0923/01/27 00:00:00.000", f.format(DfTypeUtil.toTimestamp("9230127")));
        assertEquals("2008/12/30 00:00:00.000", f.format(DfTypeUtil.toTimestamp("20081230")));
        assertEquals("2008/12/30 00:00:00.000", f.format(DfTypeUtil.toTimestamp("2008/12/30")));
        assertEquals("2008/12/30 12:34:56.000", f.format(DfTypeUtil.toTimestamp("2008/12/30 12:34:56")));
        assertEquals("2008/12/30 12:34:56.789", f.format(DfTypeUtil.toTimestamp("2008/12/30 12:34:56.789")));
        assertEquals("2008/12/30 00:00:00.000", f.format(DfTypeUtil.toTimestamp("2008-12-30")));
        assertEquals("2008/12/30 12:34:56.000", f.format(DfTypeUtil.toTimestamp("2008-12-30 12:34:56")));
        assertEquals("2008/12/30 12:34:56.789", f.format(DfTypeUtil.toTimestamp("2008-12-30 12:34:56.789")));
        assertEquals("2008/09/30 12:34:56.000", f.format(DfTypeUtil.toTimestamp("2008-09-30 12:34:56")));
        assertEquals("2008/09/30 12:34:56.000", f.format(DfTypeUtil.toTimestamp("2008-9-30 12:34:56")));
        assertEquals("2008/09/01 12:34:56.000", f.format(DfTypeUtil.toTimestamp("2008-9-1 12:34:56")));
        assertEquals("0008/09/01 12:34:56.000", f.format(DfTypeUtil.toTimestamp("8-9-1 12:34:56")));
        assertEquals("2008/09/01 00:00:00.000", f.format(DfTypeUtil.toTimestamp("2008-9-1")));
        assertEquals("0008/09/01 02:04:06.000", f.format(DfTypeUtil.toTimestamp("8-9-1 02:04:06")));
        assertEquals("0008/09/01 02:04:06.000", f.format(DfTypeUtil.toTimestamp("8-9-1 2:4:6")));
        assertEquals("2008/12/30 12:34:56.009", f.format(DfTypeUtil.toTimestamp("2008-12-30 12:34:56.9")));
    }

    // -----------------------------------------------------
    //                                                  Time
    //                                                  ----
    public void test_toTime_timestamp() {
        // ## Arrange ##
        SimpleDateFormat f = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
        Date date = DfTypeUtil.toTimestamp("2008-12-30 12:34:56.789");

        // ## Act & Assert ##
        assertNull(DfTypeUtil.toTime(null));
        assertNull(DfTypeUtil.toTime(""));
        assertEquals("1970/01/01 12:34:56.789", f.format(DfTypeUtil.toTime(date)));
    }

    public void test_toTime_various() {
        // ## Arrange ##
        SimpleDateFormat f = new SimpleDateFormat("HH:mm:ss");
        SimpleDateFormat ft = new SimpleDateFormat("HH:mm:ss.SSS");

        // ## Act & Assert ##
        assertNull(DfTypeUtil.toTime(null));
        assertNull(DfTypeUtil.toTime(""));
        assertEquals("12:34:56", f.format(DfTypeUtil.toTime("2009/12/12 12:34:56")));
        assertEquals("12:34:56", f.format(DfTypeUtil.toTime("12:34:56")));
        assertEquals("02:04:06", f.format(DfTypeUtil.toTime("02:04:06")));
        assertEquals("02:04:06", f.format(DfTypeUtil.toTime("2:4:6")));
        assertEquals("12:34:56", f.format(DfTypeUtil.toTime("12:34:56.789")));
        assertEquals("12:34:56.000", ft.format(DfTypeUtil.toTime("12:34:56.789")));
    }

    // -----------------------------------------------------
    //                                              SQL Date
    //                                              --------
    public void test_toSqlDate_basic() {
        assertNull(DfTypeUtil.toSqlDate(null));
        assertNull(DfTypeUtil.toSqlDate(""));
    }

    public void test_toSqlDate_same() {
        // ## Arrange ##
        SimpleDateFormat f = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");

        // ## Act ##
        java.sql.Date date = DfTypeUtil.toSqlDate(DfTypeUtil.toDate("2008-12-30 12:34:56.789"));

        // ## Assert ##
        assertEquals("2008/12/30 00:00:00.000", f.format(date));
    }

    public void test_toSqlDate_timestamp() {
        // ## Arrange ##
        SimpleDateFormat f = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
        Timestamp date = DfTypeUtil.toTimestamp("2008-12-30 12:34:56.789");

        // ## Act & Assert ##
        assertEquals("2008/12/30 00:00:00.000", f.format(DfTypeUtil.toSqlDate(date)));
    }

    public void test_toSqlDate_various() {
        // ## Arrange ##
        SimpleDateFormat f = new SimpleDateFormat("yyyy/MM/dd");
        SimpleDateFormat ft = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");

        // ## Act & Assert ##
        assertNull(DfTypeUtil.toSqlDate(null));
        assertNull(DfTypeUtil.toSqlDate(""));
        assertEquals("0002/01/12", f.format(DfTypeUtil.toSqlDate("20112")));
        assertEquals("0012/01/22", f.format(DfTypeUtil.toSqlDate("120122")));
        assertEquals("0923/01/27", f.format(DfTypeUtil.toSqlDate("9230127")));
        assertEquals("2008/12/30", f.format(DfTypeUtil.toSqlDate("20081230")));
        assertEquals("2008/12/30", f.format(DfTypeUtil.toSqlDate("2008/12/30")));
        assertEquals("2008/12/30", f.format(DfTypeUtil.toSqlDate("2008-12-30")));
        assertEquals("2008/12/30", f.format(DfTypeUtil.toSqlDate("2008-12-30 12:34:56")));
        assertEquals("2008/12/30", f.format(DfTypeUtil.toSqlDate("2008-12-30 12:34:56.789")));
        assertEquals("2008/09/30", f.format(DfTypeUtil.toSqlDate("2008-09-30 12:34:56")));
        assertEquals("2008/09/30", f.format(DfTypeUtil.toSqlDate("2008-9-30 12:34:56")));
        assertEquals("2008/09/01", f.format(DfTypeUtil.toSqlDate("2008-9-1 12:34:56")));
        assertEquals("0008/09/01", f.format(DfTypeUtil.toSqlDate("8-9-1 12:34:56")));
        assertEquals("2008/09/01", f.format(DfTypeUtil.toSqlDate("2008-9-1")));
        assertEquals("0008/09/01 00:00:00.000", ft.format(DfTypeUtil.toSqlDate("8-9-1 12:34:56")));
        assertEquals("2008/12/30 00:00:00.000", ft.format(DfTypeUtil.toSqlDate("2008-12-30 12:34:56.789")));
        assertEquals(java.sql.Date.class, DfTypeUtil.toSqlDate("2008-12-30 12:34:56.789").getClass());
        assertNotSame(java.util.Date.class, DfTypeUtil.toSqlDate("2008-12-30 12:34:56.789").getClass());
        assertNotSame(java.sql.Timestamp.class, DfTypeUtil.toSqlDate("2008-12-30 12:34:56.789").getClass());
    }

    // ===================================================================================
    //                                                                              Format
    //                                                                              ======
    public void test_format_Date() {
        // ## Arrange ##
        Date date = DfTypeUtil.toDate("2008/12/30 12:34:56");
        Timestamp timestamp = DfTypeUtil.toTimestamp("2008/12/30 12:34:56");

        // ## Act & Assert ##
        assertNull(DfTypeUtil.format((Date) null, "yyyy/MM/dd HH:mm:ss"));
        assertEquals("2008/12/30 12:34:56", DfTypeUtil.format(date, "yyyy/MM/dd HH:mm:ss"));
        assertEquals("2008/12/30", DfTypeUtil.format(date, "yyyy/MM/dd"));
        assertEquals("2008-12-30", DfTypeUtil.format(date, "yyyy-MM-dd"));
        assertEquals("2008-12-30 12:34:56.000", DfTypeUtil.format(date, "yyyy-MM-dd HH:mm:ss.SSS"));
        assertEquals("2008/12/30 12:34:56", DfTypeUtil.format(timestamp, "yyyy/MM/dd HH:mm:ss"));
        assertEquals("2008/12/30", DfTypeUtil.format(timestamp, "yyyy/MM/dd"));
        assertEquals("2008-12-30", DfTypeUtil.format(timestamp, "yyyy-MM-dd"));
        assertEquals("2008-12-30 12:34:56.000", DfTypeUtil.format(timestamp, "yyyy-MM-dd HH:mm:ss.SSS"));
    }
}
