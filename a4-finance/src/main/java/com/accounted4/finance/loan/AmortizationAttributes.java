package com.accounted4.finance.loan;

import java.time.LocalDate;
import javax.money.MonetaryAmount;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Bean to hold the properties required to compute amortized payments.
 *
 * @author gheinze
 */
@Getter
@Setter
@ToString
public class AmortizationAttributes {

    private MonetaryAmount loanAmount;      // original principal amount
    private MonetaryAmount regularPayment;  // monthly payment to be made, assumed monthly
    private LocalDate startDate;            // loan start date
    private LocalDate adjustmentDate;       // date from which amortization calculations commence

    @Min(1) @Max(360)
    private int termInMonths;               // number of months from the adjustment date at which amortization stops and remaining principal is due

    private boolean interestOnly;           // true if this is an interest only calculation (ie no amortization)

    @Min(1) @Max(360)
    private int amortizationPeriodInMonths; // number of months over which to amortize the payments. If payments are made till this date, principal remaining will be 0

    @Min(1) @Max(52)
    private int compoundingPeriodsPerYear;  // number of times a year interest compounding is calculated. Canadian rules: 2 (semi-annually). American rules: 12 (monthly)

    @Min(1) @Max(52)
    private int paymentFrequency;           // number of times a year payments will be made

    @Min(0) @Max(50)
    private double interestRateAsPercent;   // the nominal interest rate being paid (effective rate can be higher if compounding)

}
