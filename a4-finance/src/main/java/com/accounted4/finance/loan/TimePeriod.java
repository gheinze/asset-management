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


    private static final int MONTHS_IN_A_YEAR = 12;
    private static final int WEEKS_IN_A_YEAR = 52;

    public LocalDate getDateFrom(LocalDate fromDate, int periods) {

        if (periodsPerYear <= MONTHS_IN_A_YEAR) {
            // Incrementing in multiples of months
            return fromDate.plusMonths(MONTHS_IN_A_YEAR / periodsPerYear * periods);

        } else if (periodsPerYear == 24) { // SemiMonthly
            // Every second payment: add a month; the alternate payment 14 days after that
            return fromDate.plusMonths(periods / 2).plusDays(14 * (periods % 2));
        }

        return fromDate.plusWeeks(WEEKS_IN_A_YEAR / periodsPerYear * periods);

    }


}
