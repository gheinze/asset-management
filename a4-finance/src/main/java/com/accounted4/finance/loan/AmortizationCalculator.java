package com.accounted4.finance.loan;

import com.accounted4.finance.math.AnyUpMonetaryRounder;
import com.accounted4.finance.math.HalfUpMonetaryRounder;
import java.time.LocalDate;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;
import javax.money.Monetary;
import javax.money.MonetaryAmount;
import javax.money.MonetaryOperator;



/**
 * Utility functions for amortization schedule generation.
 *
 * @author gheinze
 */
public class AmortizationCalculator {

    private static final int STANDARD_DAYS_IN_A_YEAR = 365; // 366 in leap year
    private static final int STANDARD_WEEKS_IN_A_YEAR = 52; // 52 * 7 = 364

    private static final MonetaryOperator ANY_UP_ROUNDING_MODE = new AnyUpMonetaryRounder();
    private static final MonetaryOperator HALF_UP_ROUNDING_MODE = new HalfUpMonetaryRounder();


    // Container for library methods, so should not be instantiated
    private AmortizationCalculator() {
    }


    public static MonetaryAmount getPeriodicPayment(AmortizationAttributes amAttrs) {
        return amAttrs.isInterestOnly() ?
                getInterestOnlyPeriodicPayment(amAttrs) :
                getAmortizedPeriodicPayment(amAttrs)
                ;
    }


    private static MonetaryAmount getInterestOnlyPeriodicPayment(AmortizationAttributes amAttrs) {
        MonetaryAmount loanAmount = amAttrs.getLoanAmount();
        double interestRateAsDecimal = amAttrs.getInterestRateAsPercent() / 100.;
        int paymentFrequency = amAttrs.getPaymentFrequency();
        return loanAmount.multiply(interestRateAsDecimal / paymentFrequency).with(ANY_UP_ROUNDING_MODE);
    }


    /*
     * A = P * r / (1 - (1 + r)^-n])
     * Where:
     *   A = periodic payment
     *   P = principal amount borrowed
     *   r = periodic interest rate (as decimal)
     *   n = years
     *
     * See: https://en.wikipedia.org/wiki/Mortgage_calculator#Monthly_payment_formula
     *
     */
    private static MonetaryAmount getAmortizedPeriodicPayment(AmortizationAttributes amAttrs) {

        double loanAmount = amAttrs.getLoanAmount().getNumber().doubleValueExact();
        double periodRate = getPeriodRateAsDecimal(amAttrs);
        double amortizationYears = (double)amAttrs.getAmortizationPeriodInMonths() / 12.;

        double paymentFrequency = (double)amAttrs.getPaymentFrequency();
        double periodPayment = loanAmount * (periodRate) /
                (1.0 - Math.pow(1.0 + periodRate, -paymentFrequency * amortizationYears));

        return Monetary.getDefaultAmountFactory()
                .setCurrency(amAttrs.getLoanAmount().getCurrency())
                .setNumber(periodPayment)
                .create()
                .with(ANY_UP_ROUNDING_MODE)
                ;

    }


    /*
     * periodRate = (1 + i / c)^(c/p) - 1
     *
     * Where:
     *   i = nominal annual interest rate
     *   c = number of compounding periods per year
     *   p = number of payments per year
     *
     * See: http://www.vertex42.com/ExcelArticles/amortization-calculation.html
     */
    private static double getPeriodRateAsDecimal(AmortizationAttributes amAttrs) {
        double i = amAttrs.getInterestRateAsPercent() / 100.;
        double c = (double)amAttrs.getCompoundingPeriodsPerYear();
        double p = (double)amAttrs.getPaymentFrequency();
        return Math.pow( 1. + (i / c), c / p ) - 1.;
    }


    public static MonetaryAmount getPerDiem(MonetaryAmount amount, double annualInterestRatePercent) {
        return amount.multiply(annualInterestRatePercent * 0.01 / STANDARD_DAYS_IN_A_YEAR).with(ANY_UP_ROUNDING_MODE);
    }


    /**
     * The interest that would be owing for one period with the given terms.
     * @param amAttrs
     * @return
     */
    public static MonetaryAmount getPeriodInterest(AmortizationAttributes amAttrs) {
        return amAttrs.isInterestOnly() ?
                getInterestOnlyPeriodicPayment(amAttrs) :
                getAmortizedPeriodInterest(amAttrs)
                ;
    }

    private static MonetaryAmount getAmortizedPeriodInterest(AmortizationAttributes amAttrs) {
        double periodicRate = getPeriodRateAsDecimal(amAttrs);
        return amAttrs.getLoanAmount().multiply(periodicRate).with(HALF_UP_ROUNDING_MODE);
    }


    public static List<ScheduledPayment> generateSchedule(AmortizationAttributes amAttrs) {
        return amAttrs.isInterestOnly() ?
                generateInterestOnlySchedule(amAttrs) :
                generateAmortizedSchedule(amAttrs)
                ;
    }


    public static LocalDate getNextFirstOrFifteenthOfTheMonth(LocalDate baseDate) {

        LocalDate nextDate = null == baseDate ? LocalDate.now() : baseDate;
        int baseDayOfMonth = nextDate.getDayOfMonth();

        if (baseDayOfMonth > 15) {
            return nextDate.plusMonths(1).withDayOfMonth(1);

        } else if (baseDayOfMonth > 1 && baseDayOfMonth < 15) {
            return nextDate.withDayOfMonth(15);
        }

        assert baseDayOfMonth == 15 || baseDayOfMonth == 1;

        return LocalDate.from(nextDate);

    }


    /* Any item in the list of interest only scheduled payments can be computed without
     * reliance on previous items in the list.
    */
    private static List<ScheduledPayment> generateInterestOnlySchedule(AmortizationAttributes amAttrs) {

        return new AbstractList<ScheduledPayment>() {

            private final MonetaryAmount calculatedPayment = getPeriodicPayment(amAttrs);
            private final MonetaryAmount principal = calculatedPayment.multiply(0l);

//            private final MonetaryAmount actualPayment = getActualPayment(amAttrs);
//            private final MonetaryAmount overPayment = actualPayment.subtract(calculatedPayment);
            private final int expectedNumberOfPayments = getNumberOfExpectedPayments(amAttrs);


            @Override
            public ScheduledPayment get(int index) {

                ScheduledPayment payment =  getTemplatePayment(index, expectedNumberOfPayments, amAttrs);

                payment.setInterest(calculatedPayment);
                payment.setPrincipal(principal);
                payment.setBalance(amAttrs.getLoanAmount());

                return payment;

            }

            @Override
            public int size() {
                return expectedNumberOfPayments;
            }

        };
    }

    /* An amortized list of payments will all be pre-computed.
    */
    private static List<ScheduledPayment> generateAmortizedSchedule(AmortizationAttributes amAttrs) {

        List<ScheduledPayment> schedule = new ArrayList<>();

        final MonetaryAmount actualPayment = getActualPayment(amAttrs);
        final int expectedNumberOfPayments = getNumberOfExpectedPayments(amAttrs);

        MonetaryAmount remainingBalance = amAttrs.getLoanAmount();
        double periodicRate = getPeriodRateAsDecimal(amAttrs);

        // loop
        for (int index = 0; index < expectedNumberOfPayments; index++) {

            // Interest amounts rounding take precedence
            MonetaryAmount interest = remainingBalance.multiply(periodicRate).with(HALF_UP_ROUNDING_MODE);

            // The periodic payment is consistent, so anything that is not interest is principal
            MonetaryAmount principal = actualPayment.subtract(interest);

            if (principal.isGreaterThan(remainingBalance)) {
                principal = remainingBalance;
            }
            remainingBalance = remainingBalance.subtract(principal);

            ScheduledPayment payment =  getTemplatePayment(index, expectedNumberOfPayments, amAttrs);

            payment.setInterest(interest);
            payment.setPrincipal(principal);
            payment.setBalance(remainingBalance);

            schedule.add(payment);
        }

        return schedule;

    }


    private static int getNumberOfExpectedPayments(AmortizationAttributes amAttrs) {
        return (int) Math.ceil(amAttrs.getPaymentFrequency() * amAttrs.getTermInMonths() / 12.);
    }


    private static MonetaryAmount getActualPayment(AmortizationAttributes amAttrs) {
        return null == amAttrs.getRegularPayment() ?
            getPeriodicPayment(amAttrs) :
            amAttrs.getRegularPayment();
    }


    private static ScheduledPayment getTemplatePayment(int index, int totalPayments, AmortizationAttributes amAttrs) {

        int paymentNumber = index + 1;
        if (index < 0 || index >= totalPayments) {
            throw new IndexOutOfBoundsException(String.format("Payment number %d outside of schedule range 1 - %d", paymentNumber, totalPayments));
        }

        ScheduledPayment templatePayment = new ScheduledPayment();
        templatePayment.setPaymentNumber(paymentNumber);
        templatePayment.setPaymentDate(getPaymentDateFrom(paymentNumber, amAttrs));

        return templatePayment;
    }


    private static LocalDate getPaymentDateFrom(int paymentNumber, AmortizationAttributes amAttrs) {
        LocalDate scheduleStartDate = amAttrs.getAdjustmentDate();
        int paymentFrequency = amAttrs.getPaymentFrequency();
        TimePeriod timePeriod = TimePeriod.getTimePeriodWithPeriodCountOf(paymentFrequency);
        return timePeriod.getDateFrom(scheduleStartDate, paymentNumber);
    }


}
