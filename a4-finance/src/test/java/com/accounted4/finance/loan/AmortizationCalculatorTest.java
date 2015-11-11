package com.accounted4.finance.loan;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import javax.money.Monetary;
import javax.money.MonetaryAmount;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author gheinze
 */
public class AmortizationCalculatorTest {

    public AmortizationCalculatorTest() {
    }

    private static MonetaryAmount USD50000;
    private static MonetaryAmount USD500;

    @BeforeClass
    public static void setUpClass() {

        USD50000 = Monetary.getDefaultAmountFactory()
                .setCurrency("USD")
                .setNumber(50000)
                .create();

        USD500 = Monetary.getDefaultAmountFactory()
                .setCurrency("USD")
                .setNumber(500)
                .create();

    }


    @Test
    public void testGetPeriodicPaymentInterestOnlyMonthly() {

        AmortizationAttributes amAttrs = generateAmortizationAttributesObjectTemplate();
        amAttrs.setLoanAmount(USD50000);
        amAttrs.setPaymentFrequency(12);
        amAttrs.setInterestRateAsPercent(12.);
        amAttrs.setInterestOnly(true);
        MonetaryAmount result = AmortizationCalculator.getPeriodicPayment(amAttrs);
        assertEquals("Monthly interest-only payment", USD500, result);
    }


    @Test
    public void testGetPeriodicPaymentInterestOnlyMonthlyRoundsCeiling() {

        AmortizationAttributes amAttrs = generateAmortizationAttributesObjectTemplate();
        amAttrs.setLoanAmount(USD50000);
        amAttrs.setPaymentFrequency(12);
        amAttrs.setInterestRateAsPercent(11.);
        amAttrs.setInterestOnly(true);

        MonetaryAmount expectedResult = Monetary.getDefaultAmountFactory()
                .setCurrency("USD")
                .setNumber(458.34)
                .create();

        MonetaryAmount result = AmortizationCalculator.getPeriodicPayment(amAttrs);
        assertEquals("Monthly interest-only payment fractional penny, less than half a cent, rounds UP", expectedResult, result);
    }


    @Test
    public void testGetPeriodicPaymentInterestOnlyTimePeriods() {

        AmortizationAttributes amAttrs = generateAmortizationAttributesObjectTemplate();
        amAttrs.setLoanAmount(USD50000);
        amAttrs.setInterestRateAsPercent(10.);
        amAttrs.setInterestOnly(true);

        amAttrs.setPaymentFrequency(52);
        MonetaryAmount result = AmortizationCalculator.getPeriodicPayment(amAttrs);
        MonetaryAmount expectedResult = ofUSD(96.16);
        assertEquals("Weekly interest-only", expectedResult, result);

        amAttrs.setPaymentFrequency(26);
        result = AmortizationCalculator.getPeriodicPayment(amAttrs);
        expectedResult = ofUSD(192.31);
        assertEquals("BiWeekly interest-only", expectedResult, result);

        amAttrs.setPaymentFrequency(26);
        result = AmortizationCalculator.getPeriodicPayment(amAttrs);
        expectedResult = ofUSD(192.31);
        assertEquals("BiWeekly interest-only", expectedResult, result);

        amAttrs.setPaymentFrequency(12);
        result = AmortizationCalculator.getPeriodicPayment(amAttrs);
        expectedResult = ofUSD(416.67);
        assertEquals("Monthly interest-only", expectedResult, result);

        amAttrs.setPaymentFrequency(6);
        result = AmortizationCalculator.getPeriodicPayment(amAttrs);
        expectedResult = ofUSD(833.34);
        assertEquals("BiMonthly interest-only", expectedResult, result);

        amAttrs.setPaymentFrequency(4);
        result = AmortizationCalculator.getPeriodicPayment(amAttrs);
        expectedResult = ofUSD(1250);
        assertEquals("Quarterly interest-only", expectedResult, result);

        amAttrs.setPaymentFrequency(2);
        result = AmortizationCalculator.getPeriodicPayment(amAttrs);
        expectedResult = ofUSD(2500);
        assertEquals("Semiannual interest-only", expectedResult, result);

        amAttrs.setPaymentFrequency(1);
        result = AmortizationCalculator.getPeriodicPayment(amAttrs);
        expectedResult = ofUSD(5000);
        assertEquals("Annual interest-only", expectedResult, result);

    }


    @Test
    public void testGetPeriodicPaymentAmortized() {
        AmortizationAttributes amAttrs = generateAmortizationAttributesObjectTemplate();
        amAttrs.setLoanAmount(USD50000.multiply(2));
        amAttrs.setInterestRateAsPercent(12.);
        amAttrs.setCompoundingPeriodsPerYear(2);
        amAttrs.setPaymentFrequency(12);
        MonetaryAmount result = AmortizationCalculator.getPeriodicPayment(amAttrs);
        MonetaryAmount expectedResult = ofUSD(1031.90);
        assertEquals("Monthly amortized payment compounded semi-annually", expectedResult, result);
    }


    @Test
    public void testGetPeriodicPaymentAmortizedMonthlyCompoundPeriod() {
        AmortizationAttributes amAttrs = generateAmortizationAttributesObjectTemplate();
        amAttrs.setLoanAmount(USD50000.multiply(2));
        amAttrs.setInterestRateAsPercent(12.);
        amAttrs.setCompoundingPeriodsPerYear(12);
        amAttrs.setPaymentFrequency(12);
        MonetaryAmount result = AmortizationCalculator.getPeriodicPayment(amAttrs);
        MonetaryAmount expectedResult = ofUSD(1053.23);
        assertEquals("Monthly amortized payment compounded monthly", expectedResult, result);
    }


    @Test
    public void testGetPeriodicPaymentCompoundSemiPaymentWeekly() {
        AmortizationAttributes amAttrs = generateAmortizationAttributesObjectTemplate();
        amAttrs.setLoanAmount(USD50000.divide(5));
        amAttrs.setInterestRateAsPercent(10.);
        amAttrs.setCompoundingPeriodsPerYear(TimePeriod.SemiAnnually.getPeriodsPerYear());
        amAttrs.setAmortizationPeriodInMonths(12);
        amAttrs.setPaymentFrequency(TimePeriod.Weekly.getPeriodsPerYear());
        MonetaryAmount result = AmortizationCalculator.getPeriodicPayment(amAttrs);
        MonetaryAmount expectedResult = ofUSD(202.04);
        assertEquals("Amortized, compounded semi-annual, payment weekly", expectedResult, result);
    }


    @Test
    public void testGetPeriodicPaymentCompoundSemiPaymentBiWeekly() {
        AmortizationAttributes amAttrs = generateAmortizationAttributesObjectTemplate();
        amAttrs.setLoanAmount(USD50000.divide(5));
        amAttrs.setInterestRateAsPercent(10.);
        amAttrs.setCompoundingPeriodsPerYear(TimePeriod.SemiAnnually.getPeriodsPerYear());
        amAttrs.setAmortizationPeriodInMonths(12);
        amAttrs.setPaymentFrequency(TimePeriod.BiWeekly.getPeriodsPerYear());
        MonetaryAmount result = AmortizationCalculator.getPeriodicPayment(amAttrs);
        MonetaryAmount expectedResult = ofUSD(404.45);
        assertEquals("Amortized, compounded semi-annual, payment biweekly", expectedResult, result);
    }


    @Test
    public void testGetPeriodicPaymentCompoundSemiPaymentSemiMonthly() {
        AmortizationAttributes amAttrs = generateAmortizationAttributesObjectTemplate();
        amAttrs.setLoanAmount(USD50000.divide(5));
        amAttrs.setInterestRateAsPercent(10.);
        amAttrs.setCompoundingPeriodsPerYear(TimePeriod.SemiAnnually.getPeriodsPerYear());
        amAttrs.setAmortizationPeriodInMonths(12);
        amAttrs.setPaymentFrequency(TimePeriod.SemiMonthly.getPeriodsPerYear());
        MonetaryAmount result = AmortizationCalculator.getPeriodicPayment(amAttrs);
        MonetaryAmount expectedResult = ofUSD(438.22);
        assertEquals("Amortized, compounded semi-annual, payment semimonthly", expectedResult, result);
    }


    @Test
    public void testGetPeriodicPaymentCompoundSemiPaymentMonthly() {
        AmortizationAttributes amAttrs = generateAmortizationAttributesObjectTemplate();
        amAttrs.setLoanAmount(USD50000.divide(5));
        amAttrs.setInterestRateAsPercent(10.);
        amAttrs.setCompoundingPeriodsPerYear(TimePeriod.SemiAnnually.getPeriodsPerYear());
        amAttrs.setAmortizationPeriodInMonths(12);
        amAttrs.setPaymentFrequency(TimePeriod.Monthly.getPeriodsPerYear());
        MonetaryAmount result = AmortizationCalculator.getPeriodicPayment(amAttrs);
        MonetaryAmount expectedResult = ofUSD(878.22);
        assertEquals("Amortized, compounded semi-annual, payment monthly", expectedResult, result);
    }


    @Test
    public void testGetPeriodicPaymentCompoundSemiPaymentBiMonthly() {
        AmortizationAttributes amAttrs = generateAmortizationAttributesObjectTemplate();
        amAttrs.setLoanAmount(USD50000.divide(5));
        amAttrs.setInterestRateAsPercent(10.);
        amAttrs.setCompoundingPeriodsPerYear(TimePeriod.SemiAnnually.getPeriodsPerYear());
        amAttrs.setAmortizationPeriodInMonths(12);
        amAttrs.setPaymentFrequency(TimePeriod.BiMonthly.getPeriodsPerYear());
        MonetaryAmount result = AmortizationCalculator.getPeriodicPayment(amAttrs);
        MonetaryAmount expectedResult = ofUSD(1763.61);
        assertEquals("Amortized, compounded semi-annual, payment semimonthly", expectedResult, result);
    }


    @Test
    public void testGetPeriodicPaymentCompoundSemiPaymentQuarterly() {
        AmortizationAttributes amAttrs = generateAmortizationAttributesObjectTemplate();
        amAttrs.setLoanAmount(USD50000.divide(5));
        amAttrs.setInterestRateAsPercent(10.);
        amAttrs.setCompoundingPeriodsPerYear(TimePeriod.SemiAnnually.getPeriodsPerYear());
        amAttrs.setAmortizationPeriodInMonths(12);
        amAttrs.setPaymentFrequency(TimePeriod.Quarterly.getPeriodsPerYear());
        MonetaryAmount result = AmortizationCalculator.getPeriodicPayment(amAttrs);
        MonetaryAmount expectedResult = ofUSD(2656.23);
        assertEquals("Amortized, compounded semi-annual, payment semimonthly", expectedResult, result);
    }


    @Test
    public void testGetPeriodicPaymentCompoundSemiPaymentSemiAnnually() {
        AmortizationAttributes amAttrs = generateAmortizationAttributesObjectTemplate();
        amAttrs.setLoanAmount(USD50000.divide(5));
        amAttrs.setInterestRateAsPercent(10.);
        amAttrs.setCompoundingPeriodsPerYear(TimePeriod.SemiAnnually.getPeriodsPerYear());
        amAttrs.setAmortizationPeriodInMonths(12);
        amAttrs.setPaymentFrequency(TimePeriod.SemiAnnually.getPeriodsPerYear());
        MonetaryAmount result = AmortizationCalculator.getPeriodicPayment(amAttrs);
        MonetaryAmount expectedResult = ofUSD(5378.05);
        assertEquals("Amortized, compounded semi-annual, payment semimonthly", expectedResult, result);
    }


    @Test
    public void testGetPerDiem() {
        MonetaryAmount result = AmortizationCalculator.getPerDiem(USD50000, 10.);
        MonetaryAmount expectedResult = ofUSD(13.70);
        assertEquals("Per diem", expectedResult, result);
    }


    @Test
    public void testGenerateScheduleInterestOnlyMonthly() {
        AmortizationAttributes amAttrs = generateAmortizationAttributesObjectTemplate();
        amAttrs.setLoanAmount(ofUSD(10000));
        amAttrs.setInterestRateAsPercent(10.);
        amAttrs.setInterestOnly(true);
        amAttrs.setPaymentFrequency(TimePeriod.Monthly.getPeriodsPerYear());
        int termInMonths = 12;
        amAttrs.setTermInMonths(termInMonths);
        amAttrs.setAdjustmentDate(LocalDate.of(2016, Month.JANUARY, 1));
        amAttrs.setRegularPayment(ofUSD(0));

        List<ScheduledPayment> schedule = AmortizationCalculator.generateSchedule(amAttrs);
        assertEquals("Interest only schedule term in months", termInMonths, schedule.size());

        MonetaryAmount expectedInterest = ofUSD(83.34);
        MonetaryAmount expectedPrincipal = ofUSD(0);
        LocalDate expectedDate = amAttrs.getAdjustmentDate();
        int index = 0;

        for (ScheduledPayment payment : schedule) {
            expectedDate = expectedDate.plusMonths(1L);
            assertEquals("Interest only schedule payment number", ++index, payment.getPaymentNumber());
            assertEquals("Interest only schedule payment date", expectedDate, payment.getPaymentDate());
            assertEquals("Interest only schedule payment interest", expectedInterest, payment.getInterest());
            assertEquals("Interest only schedule payment principal", expectedPrincipal, payment.getPrincipal());
            assertEquals("Interest only schedule payment total payment", expectedInterest, payment.getPayment());
            assertEquals("Interest only schedule payment loan principal", ofUSD(10000), payment.getBalance());
        }

    }


    @Test
    public void testGenerateScheduleInterestOnlyMonthlyWithOverPayment() {
        AmortizationAttributes amAttrs = generateAmortizationAttributesObjectTemplate();
        MonetaryAmount loanAmount = ofUSD(10000);
        amAttrs.setLoanAmount(loanAmount);
        amAttrs.setInterestRateAsPercent(10.);
        amAttrs.setInterestOnly(true);
        amAttrs.setPaymentFrequency(TimePeriod.Monthly.getPeriodsPerYear());
        int termInMonths = 12;
        amAttrs.setTermInMonths(termInMonths);
        amAttrs.setAdjustmentDate(LocalDate.of(2016, Month.JANUARY, 1));

        MonetaryAmount overPayment = ofUSD(10);
        MonetaryAmount expectedInterest = ofUSD(83.34);
        MonetaryAmount expectedPrincipal = overPayment;
        MonetaryAmount expectedPayment = expectedInterest.add(overPayment);
        amAttrs.setRegularPayment(expectedInterest.add(overPayment));

        List<ScheduledPayment> schedule = AmortizationCalculator.generateSchedule(amAttrs);
        assertEquals("Interest only schedule term in months", termInMonths, schedule.size());

        LocalDate expectedDate = amAttrs.getAdjustmentDate();
        int paymentNumber = 0;

        for (ScheduledPayment payment : schedule) {
            expectedDate = expectedDate.plusMonths(1L);
            paymentNumber += 1;
            assertEquals("Interest only schedule with over payment: payment number", paymentNumber, payment.getPaymentNumber());
            assertEquals("Interest only schedule with over payment: payment date", expectedDate, payment.getPaymentDate());
            assertEquals("Interest only schedule with over payment: payment interest", expectedInterest, payment.getInterest());
            assertEquals("Interest only schedule with over payment: payment principal", expectedPrincipal, payment.getPrincipal());
            assertEquals("Interest only schedule with over payment: payment total payment", expectedPayment, payment.getPayment());
            assertEquals("Interest only schedule with over payment: payment loan principal", loanAmount.subtract(overPayment.multiply(paymentNumber)), payment.getBalance());
        }

    }


    @Test
    public void testGenerateScheduleInterestOnlyArrayBounds() {
        AmortizationAttributes amAttrs = generateAmortizationAttributesObjectTemplate();
        amAttrs.setLoanAmount(ofUSD(10000));
        amAttrs.setInterestRateAsPercent(12.);
        amAttrs.setInterestOnly(true);
        amAttrs.setPaymentFrequency(TimePeriod.Weekly.getPeriodsPerYear());
        int termInMonths = 24;
        amAttrs.setTermInMonths(termInMonths);
        amAttrs.setAdjustmentDate(LocalDate.of(2016, Month.JANUARY, 1));

        List<ScheduledPayment> schedule = AmortizationCalculator.generateSchedule(amAttrs);

        try {
            schedule.get(-1);
            fail("Interest only schedule should not allow negative payment number");
        } catch(IndexOutOfBoundsException iobe) {
        }

        int expectedPayments = (int) Math.ceil(amAttrs.getPaymentFrequency() * amAttrs.getTermInMonths() / 12.);

        try {
            schedule.get(expectedPayments);
            fail("Interest only schedule should not allow payments beyond schedule bound");
        } catch(IndexOutOfBoundsException iobe) {
        }

        schedule.get(expectedPayments - 1);

    }


    @Test
    public void testGetPaymentsAmortized() {

        AmortizationAttributes amAttrs = generateAmortizationAttributesObjectTemplate();
        amAttrs.setLoanAmount(ofUSD(200000));
        amAttrs.setInterestRateAsPercent(8.);
        amAttrs.setInterestOnly(false);
        amAttrs.setRegularPayment(ofUSD(0));
        amAttrs.setPaymentFrequency(TimePeriod.Monthly.getPeriodsPerYear());
        int termInMonths = 36;
        amAttrs.setTermInMonths(termInMonths);
        amAttrs.setAmortizationPeriodInMonths(20 * 12);
        amAttrs.setCompoundingPeriodsPerYear(2);

        List<ScheduledPayment> schedule = AmortizationCalculator.generateSchedule(amAttrs);

        assertEquals("Amortized payment count", termInMonths, schedule.size());

        MonetaryAmount interestTotal = schedule.stream()
                .map(payment -> payment.getInterest())
                .reduce((a, b) -> a.add(b))
                .get()
                ;

        assertEquals("Amortized Interest total", ofUSD(45681.29), interestTotal);

    }


    @Test
    public void testGetPaymentsAmortizedFully() {

        AmortizationAttributes amAttrs = generateAmortizationAttributesObjectTemplate();
        MonetaryAmount loanAmount = ofUSD(10000);
        amAttrs.setLoanAmount(loanAmount);
        amAttrs.setInterestRateAsPercent(7.);
        amAttrs.setInterestOnly(false);
        amAttrs.setRegularPayment(ofUSD(0));
        amAttrs.setPaymentFrequency(TimePeriod.Monthly.getPeriodsPerYear());
        int termInMonths = 12;
        amAttrs.setTermInMonths(termInMonths);
        amAttrs.setAmortizationPeriodInMonths(12);
        amAttrs.setCompoundingPeriodsPerYear(2);

        List<ScheduledPayment> schedule = AmortizationCalculator.generateSchedule(amAttrs);

        MonetaryAmount interestTotal = schedule.stream()
                .map(payment -> payment.getInterest())
                .reduce((a, b) -> a.add(b))
                .get()
                ;

        assertEquals("Amortized Interest total", ofUSD(377.67), interestTotal);

        MonetaryAmount principalTotal = schedule.stream()
                .map(payment -> payment.getPrincipal())
                .reduce((a, b) -> a.add(b))
                .get()
                ;

        assertEquals("Amortized Principal fully paid", loanAmount, principalTotal);

    }


    @Test
    public void testGetPaymentsAmortizedWithOverPayment() {

        AmortizationAttributes amAttrs = generateAmortizationAttributesObjectTemplate();
        amAttrs.setLoanAmount(ofUSD(10000));
        amAttrs.setInterestRateAsPercent(3.);
        amAttrs.setInterestOnly(false);
        amAttrs.setRegularPayment(ofUSD(400));
        amAttrs.setPaymentFrequency(TimePeriod.SemiMonthly.getPeriodsPerYear());
        int termInMonths = 24;
        amAttrs.setTermInMonths(termInMonths);
        amAttrs.setAmortizationPeriodInMonths(24);
        amAttrs.setCompoundingPeriodsPerYear(2);

        List<ScheduledPayment> schedule = AmortizationCalculator.generateSchedule(amAttrs);

        assertEquals("Amortized payment count", amAttrs.getPaymentFrequency() * termInMonths / 12, schedule.size());

        MonetaryAmount interestTotal = schedule.stream()
                .map(payment -> payment.getInterest())
                .reduce((a, b) -> a.add(b))
                .get()
                ;

        assertEquals("Amortized Interest total", ofUSD(164.81), interestTotal);

    }


    private AmortizationAttributes generateAmortizationAttributesObjectTemplate() {

        AmortizationAttributes amAttrs = new AmortizationAttributes();
        amAttrs.setLoanAmount(USD50000);
        amAttrs.setRegularPayment(USD50000.divide(100));
        amAttrs.setStartDate(LocalDate.of(2015, Month.DECEMBER, 28));
        amAttrs.setAdjustmentDate(LocalDate.of(2016, Month.JANUARY, 1));
        amAttrs.setTermInMonths(24);
        amAttrs.setInterestOnly(false);
        amAttrs.setAmortizationPeriodInMonths(300);
        amAttrs.setCompoundingPeriodsPerYear(2);
        amAttrs.setPaymentFrequency(12);
        amAttrs.setInterestRateAsPercent(12.);

        return amAttrs;
    }

    private MonetaryAmount ofUSD(double amount) {
        return Monetary.getDefaultAmountFactory()
                .setCurrency("USD")
                .setNumber(amount)
                .create();

    }
}
