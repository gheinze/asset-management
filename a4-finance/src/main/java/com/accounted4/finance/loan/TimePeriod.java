package com.accounted4.finance.loan;

import java.time.LocalDate;
import lombok.Getter;

/**
 *
 * @author gheinze
 */
public enum TimePeriod {

     Weekly(52)
    ,BiWeekly(26)
    ,SemiMonthly(24)
    ,Monthly(12, true)
    ,BiMonthly(6)
    ,Quarterly(4)
    ,SemiAnnually(2, true)
    ,Annually(1, true)
    ;


    @Getter private final int periodsPerYear;
    @Getter private final boolean compoundingPeriod;


    private TimePeriod(int periodsPerYear) {
        this(periodsPerYear, false);
    }

    private TimePeriod(int periodsPerYear, boolean compoundingPeriod) {
        this.periodsPerYear = periodsPerYear;
        this.compoundingPeriod = compoundingPeriod;
    }

    public String getDisplayName() {
        return this.name();
    }


    public static TimePeriod getTimePeriodWithPeriodCountOf(int periodsPerYear) {
        for (TimePeriod period : TimePeriod.values()) {
            if (period.periodsPerYear == periodsPerYear) {
                return period;
            }
        }
        throw new RuntimeException("TimePeriod not found with " + periodsPerYear + " periods a year.");
    }


    private static final long MONTHS_IN_A_YEAR = 12L;
    private static final long WEEKS_IN_A_YEAR = 52L;

    public LocalDate getDateFrom(LocalDate fromDate, long periods) {

        if (periodsPerYear <= MONTHS_IN_A_YEAR) {
            // Incrementing in multiples of months
            return fromDate.plusMonths(MONTHS_IN_A_YEAR / periodsPerYear * periods);

        } else if (periodsPerYear == 24) { // SemiMonthly
            // Every second payment: add a month; the alternate payment 14 days after that
            return fromDate.plusMonths(periods / 2L).plusDays(14L * (periods % 2L));
        }

        return fromDate.plusWeeks(WEEKS_IN_A_YEAR / periodsPerYear * periods);

    }


}
