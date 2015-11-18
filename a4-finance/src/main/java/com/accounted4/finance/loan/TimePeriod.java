package com.accounted4.finance.loan;

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


    //private Function<AmortizationAttributes, Double> periodRateCalculator;


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

}
