package com.accounted4.finance.loan;

import java.time.LocalDate;
import javax.money.MonetaryAmount;
import lombok.Getter;
import lombok.Setter;

/**
 * Bean to hold the properties required to compute amortized payments.
 *
 * @author gheinze
 */
@Getter
@Setter
public class AmortizationAttributes {

    private MonetaryAmount loanAmount;      // original principal amount
    private MonetaryAmount regularPayment;  // monthly payment to be made, assumed monthly
    private LocalDate startDate;            // loan start date
    private LocalDate adjustmentDate;       // date from which amortization calculations commence
    private int termInMonths;               // number of months from the adjustment date at which amortization stops and remaining principal is due
    private boolean interestOnly;           // true if this is an interest only calculation (ie no amortization)
    private int amortizationPeriodInMonths; // number of months over which to amortize the payments. If payments are made till this date, principal remaining will be 0
    private int compoundingPeriodsPerYear;  // number of times a year interest compounding is calculated. Canadian rules: 2 (semi-annually). American rules: 12 (monthly)
    private int paymentFrequency;           // number of times a year payments will be made
    private double interestRateAsPercent;   // the nominal interest rate being paid (effective rate can be higher if compounding)

}
