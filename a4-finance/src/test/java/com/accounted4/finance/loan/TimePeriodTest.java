package com.accounted4.finance.loan;

import java.time.LocalDate;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author gheinze
 */
public class TimePeriodTest {

    @Test
    public void testGetTimePeriodWithPeriodCountOf() {

        int periodsPerYear;
        TimePeriod result;

        periodsPerYear = 52;
        result = TimePeriod.getTimePeriodWithPeriodCountOf(periodsPerYear);
        assertEquals(TimePeriod.Weekly.getDisplayName(), TimePeriod.Weekly, result);

        periodsPerYear = 26;
        result = TimePeriod.getTimePeriodWithPeriodCountOf(periodsPerYear);
        assertEquals(TimePeriod.BiWeekly.getDisplayName(), TimePeriod.BiWeekly, result);

        periodsPerYear = 24;
        result = TimePeriod.getTimePeriodWithPeriodCountOf(periodsPerYear);
        assertEquals(TimePeriod.SemiMonthly.getDisplayName(), TimePeriod.SemiMonthly, result);

        periodsPerYear = 12;
        result = TimePeriod.getTimePeriodWithPeriodCountOf(periodsPerYear);
        assertEquals(TimePeriod.Monthly.getDisplayName(), TimePeriod.Monthly, result);

        periodsPerYear = 6;
        result = TimePeriod.getTimePeriodWithPeriodCountOf(periodsPerYear);
        assertEquals(TimePeriod.BiMonthly.getDisplayName(), TimePeriod.BiMonthly, result);

        periodsPerYear = 4;
        result = TimePeriod.getTimePeriodWithPeriodCountOf(periodsPerYear);
        assertEquals(TimePeriod.Quarterly.getDisplayName(), TimePeriod.Quarterly, result);

        periodsPerYear = 2;
        result = TimePeriod.getTimePeriodWithPeriodCountOf(periodsPerYear);
        assertEquals(TimePeriod.SemiAnnually.getDisplayName(), TimePeriod.SemiAnnually, result);

        periodsPerYear = 1;
        result = TimePeriod.getTimePeriodWithPeriodCountOf(periodsPerYear);
        assertEquals(TimePeriod.Annually.getDisplayName(), TimePeriod.Annually, result);

    }


    @Test(expected = RuntimeException.class)
    public void testGetTimePeriodWithInvalidPeriodCount() {

        int periodsPerYear;
        TimePeriod result;

        periodsPerYear = 5;
        result = TimePeriod.getTimePeriodWithPeriodCountOf(periodsPerYear);
        fail("Trying to find a TimePeriod for a non-configured value of periods per year.");
    }


    @Test
    public void testGetDateFromWeekly() {

        LocalDate startDate = LocalDate.of(2016, 1, 1);
        LocalDate expectedDate = LocalDate.of(2016, 1, 8);
        LocalDate result = TimePeriod.Weekly.getDateFrom(startDate, 1);
        assertEquals("Adding 1 week", expectedDate, result);

        startDate = LocalDate.of(2016, 1, 1);
        expectedDate = LocalDate.of(2016, 2, 5);
        result = TimePeriod.Weekly.getDateFrom(startDate, 5);
        assertEquals("Adding 5 weeks", expectedDate, result);

        startDate = LocalDate.of(2016, 1, 1);
        expectedDate = LocalDate.of(2016, 1, 1);
        result = TimePeriod.Weekly.getDateFrom(startDate, 0);
        assertEquals("Adding 0 weeks", expectedDate, result);

        startDate = LocalDate.of(2016, 1, 1);
        expectedDate = LocalDate.of(2015, 12, 25);
        result = TimePeriod.Weekly.getDateFrom(startDate, -1);
        assertEquals("Adding -1 week", expectedDate, result);

    }


    @Test
    public void testGetDateFromBiWeekly() {

        LocalDate startDate = LocalDate.of(2016, 1, 1);
        LocalDate expectedDate = LocalDate.of(2016, 1, 15);
        LocalDate result = TimePeriod.BiWeekly.getDateFrom(startDate, 1);
        assertEquals("Adding 1 2-week period", expectedDate, result);

        startDate = LocalDate.of(2016, 1, 1);
        expectedDate = LocalDate.of(2016, 2, 12);
        result = TimePeriod.BiWeekly.getDateFrom(startDate, 3);
        assertEquals("Adding 3 2-week periods", expectedDate, result);

    }


    @Test
    public void testGetDateFromSemiMonthly() {

        LocalDate startDate = LocalDate.of(2016, 1, 1);
        LocalDate expectedDate = LocalDate.of(2016, 1, 15);
        LocalDate result = TimePeriod.SemiMonthly.getDateFrom(startDate, 1);
        assertEquals("Adding 1 semi-monthly period", expectedDate, result);

        startDate = LocalDate.of(2016, 1, 1);
        expectedDate = LocalDate.of(2016, 2, 1);
        result = TimePeriod.SemiMonthly.getDateFrom(startDate, 2);
        assertEquals("Adding 2 semi-monthly periods", expectedDate, result);

        startDate = LocalDate.of(2016, 1, 1);
        expectedDate = LocalDate.of(2016, 2, 15);
        result = TimePeriod.SemiMonthly.getDateFrom(startDate, 3);
        assertEquals("Adding 3 semi-monthly periods", expectedDate, result);

        startDate = LocalDate.of(2015, 1, 30);
        expectedDate = LocalDate.of(2015, 2, 28);
        result = TimePeriod.SemiMonthly.getDateFrom(startDate, 2);
        assertEquals("Semi-monthly period respects end of month dates", expectedDate, result);

    }


    @Test
    public void testGetDateFromMonthly() {

        LocalDate startDate = LocalDate.of(2015, 11, 7);
        LocalDate expectedDate = LocalDate.of(2016, 1, 7);
        LocalDate result = TimePeriod.Monthly.getDateFrom(startDate, 2);
        assertEquals("Adding 2 months", expectedDate, result);

    }


    @Test
    public void testGetDateFromBiMonthly() {

        LocalDate startDate = LocalDate.of(2015, 11, 7);
        LocalDate expectedDate = LocalDate.of(2016, 1, 7);
        LocalDate result = TimePeriod.BiMonthly.getDateFrom(startDate, 1);
        assertEquals("Adding bi-monthly", expectedDate, result);

    }


    @Test
    public void testGetDateFromQuarterly() {

        LocalDate startDate = LocalDate.of(2015, 10, 7);
        LocalDate expectedDate = LocalDate.of(2016, 1, 7);
        LocalDate result = TimePeriod.Quarterly.getDateFrom(startDate, 1);
        assertEquals("Adding quarterly", expectedDate, result);

    }


    @Test
    public void testGetDateFromSemiAnnually() {

        LocalDate startDate = LocalDate.of(2016, 1, 1);
        LocalDate expectedDate = LocalDate.of(2016, 7, 1);
        LocalDate result = TimePeriod.SemiAnnually.getDateFrom(startDate, 1);
        assertEquals("Adding semi-annually", expectedDate, result);

    }


    @Test
    public void testGetDateFromAnnually() {

        LocalDate startDate = LocalDate.of(2016, 1, 1);
        LocalDate expectedDate = LocalDate.of(2015, 1, 1);
        LocalDate result = TimePeriod.Annually.getDateFrom(startDate, -1);
        assertEquals("Adding annually", expectedDate, result);

    }


}
