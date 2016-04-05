/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.accounted4.assetmanager.ui.loan;

import com.accounted4.assetmanager.ui.loan.status.LoanStatus;
import com.accounted4.assetmanager.entity.Loan;
import com.accounted4.assetmanager.entity.LoanCharge;
import com.accounted4.assetmanager.entity.LoanTerms;
import com.accounted4.finance.loan.AmortizationAttributes;
import com.accounted4.finance.loan.TimePeriod;
import java.time.LocalDate;
import java.util.HashSet;
import javax.money.Monetary;
import javax.money.MonetaryAmount;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author gheinze
 */
public class LoanStatusTest {

    private static MonetaryAmount CAD1000;
    private static AmortizationAttributes defaultAmortizationAttributes;


    public LoanStatusTest() {
    }

    @BeforeClass
    public static void setUpClass() {

        CAD1000 = Monetary.getDefaultAmountFactory()
                .setCurrency("CAD")
                .setNumber(1000)
                .create();

        defaultAmortizationAttributes = AmortizationAttributes.getDefaultInstance(CAD1000);

    }

//    @Test
//    public void testGetOrderedLineItems() {
//        System.out.println("getOrderedLineItems");
//        LoanStatus instance = null;
//        List<LoanStatusLineItem> expResult = null;
//        List<LoanStatusLineItem> result = instance.getOrderedLineItems();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    @Test
//    public void testGetLoanName() {
//        System.out.println("getLoanName");
//        LoanStatus instance = null;
//        String expResult = "";
//        String result = instance.getLoanName();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    @Test
//    public void testGetRegularDue() {
//        System.out.println("getRegularDue");
//        LoanStatus instance = null;
//        MonetaryAmount expResult = null;
//        MonetaryAmount result = instance.getRegularDue();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    @Test
//    public void testGetActualDue() {
//        System.out.println("getActualDue");
//        LoanStatus instance = null;
//        MonetaryAmount expResult = null;
//        MonetaryAmount result = instance.getActualDue();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    @Test
//    public void testGetBalance() {
//        System.out.println("getBalance");
//        LoanStatus instance = null;
//        MonetaryAmount expResult = null;
//        MonetaryAmount result = instance.getBalance();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    @Test
//    public void testGetNextChequeOnFile() {
//        System.out.println("getNextChequeOnFile");
//        LoanStatus instance = null;
//        String expResult = "";
//        String result = instance.getNextChequeOnFile();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    @Test
//    public void testGetDaysToMaturity() {
//        System.out.println("getDaysToMaturity");
//        LoanStatus instance = null;
//        Long expResult = null;
//        Long result = instance.getDaysToMaturity();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    @Test
//    public void testGetPerDiem() {
//        System.out.println("getPerDiem");
//        LoanStatus instance = null;
//        MonetaryAmount expResult = null;
//        MonetaryAmount result = instance.getPerDiem();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    @Test
//    public void testGetLastScheduledPaymentDate() {
//        System.out.println("getLastScheduledPaymentDate");
//        LoanStatus instance = null;
//        LocalDate expResult = null;
//        LocalDate result = instance.getLastScheduledPaymentDate();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }

    @Test
    public void testGetNextScheduledPaymentDateIsToday() {
        System.out.println("getNextScheduledPaymentDateIsToday");

        final LocalDate now = LocalDate.now();

        LoanTerms terms = new LoanTerms();
        terms.refreshFrom(defaultAmortizationAttributes);
        terms.setStartDate(now);
        terms.setTermInMonths(12);
        terms.setPaymentFrequency(TimePeriod.Monthly.getPeriodsPerYear());

        Loan loan = new Loan();
        loan.setTerms(terms);
        loan.setCharges(new HashSet<>());
        loan.setPayments(new HashSet<>());

        LoanStatus loanStatus = new LoanStatus(loan);

        LocalDate expResult = now;
        LocalDate result = loanStatus.getNextScheduledPaymentDate();

        assertEquals("Next scheduled payment date is today.", expResult, result);

    }

    @Test
    public void testGetNextScheduledPaymentDateIsNextPeriod() {
        System.out.println("getNextScheduledPaymentDateIsNextPeriod");

        final LocalDate now = LocalDate.now();

        LoanTerms terms = new LoanTerms();
        terms.refreshFrom(defaultAmortizationAttributes);
        terms.setStartDate(now.plusDays(-1));
        terms.setTermInMonths(12);
        terms.setPaymentFrequency(TimePeriod.Monthly.getPeriodsPerYear());

        Loan loan = new Loan();
        loan.setTerms(terms);
        loan.setCharges(new HashSet<>());
        loan.setPayments(new HashSet<>());

        LoanStatus loanStatus = new LoanStatus(loan);

        TimePeriod paymentFrequency = TimePeriod.getTimePeriodWithPeriodCountOf(terms.getPaymentFrequency());
        LocalDate expResult = paymentFrequency.getDateFrom(terms.getStartDate(), 1);
        LocalDate result = loanStatus.getNextScheduledPaymentDate();

        assertEquals("Next scheduled payment in the future.", expResult, result);

    }

    @Test
    public void testGetNextScheduledPaymentDateClosed() {
        System.out.println("getNextScheduledPaymentDateClosed");

        final LocalDate now = LocalDate.now();

        LoanTerms terms = new LoanTerms();
        terms.refreshFrom(defaultAmortizationAttributes);
        terms.setStartDate(now.minusMonths(14));
        terms.setTermInMonths(12);
        terms.setPaymentFrequency(TimePeriod.Monthly.getPeriodsPerYear());

        Loan loan = new Loan();
        loan.setTerms(terms);
        loan.setCharges(new HashSet<>());
        loan.setPayments(new HashSet<>());
        loan.setCloseDate(now.minusDays(1));

        LoanStatus loanStatus = new LoanStatus(loan);

        // The loan came to term two months ago: doesn't matter
        // The loan was closed yesterday: scheduled date is the period date before yesterday: 1 month ago

        TimePeriod paymentFrequency = TimePeriod.getTimePeriodWithPeriodCountOf(terms.getPaymentFrequency());
        LocalDate expResult = paymentFrequency.getDateFrom(now, -1);
        LocalDate result = loanStatus.getNextScheduledPaymentDate();

        assertEquals("Next scheduled payment for closed loan is before loan was closed.", expResult, result);

    }


    @Test
    public void testGetCurrentScheduledPaymentDateIsToday() {
        System.out.println("getCurrentScheduledPaymentDateIsToday");

        final LocalDate now = LocalDate.now();

        LoanTerms terms = new LoanTerms();
        terms.refreshFrom(defaultAmortizationAttributes);
        terms.setStartDate(now.minusMonths(1));
        terms.setTermInMonths(12);
        terms.setPaymentFrequency(TimePeriod.Monthly.getPeriodsPerYear());

        Loan loan = new Loan();
        loan.setTerms(terms);
        loan.setCharges(new HashSet<>());
        loan.setPayments(new HashSet<>());

        LoanStatus loanStatus = new LoanStatus(loan);

        LocalDate expResult = now;
        LocalDate result = loanStatus.getCurrentScheduledPaymentDate();

        assertEquals("Current scheduled payment date is today.", expResult, result);

    }


    @Test
    public void testGetCurrentScheduledPaymentDateIsLastPeriod() {
        System.out.println("getCurrentScheduledPaymentDateIsLastPeriod");

        final LocalDate now = LocalDate.now();

        LoanTerms terms = new LoanTerms();
        terms.refreshFrom(defaultAmortizationAttributes);
        terms.setStartDate(now.plusDays(-1));
        terms.setTermInMonths(12);
        terms.setPaymentFrequency(TimePeriod.Monthly.getPeriodsPerYear());

        Loan loan = new Loan();
        loan.setTerms(terms);
        loan.setCharges(new HashSet<>());
        loan.setPayments(new HashSet<>());

        LoanStatus loanStatus = new LoanStatus(loan);

        LocalDate expResult = terms.getStartDate();
        LocalDate result = loanStatus.getCurrentScheduledPaymentDate();

        assertEquals("Current scheduled payment in the past.", expResult, result);

    }


    @Test
    public void testGetCurrentScheduledPaymentDateClosed() {
        System.out.println("getCurrentScheduledPaymentDateClosed");

        final LocalDate now = LocalDate.now();

        LoanTerms terms = new LoanTerms();
        terms.refreshFrom(defaultAmortizationAttributes);
        terms.setStartDate(now.minusMonths(14));
        terms.setTermInMonths(12);
        terms.setPaymentFrequency(TimePeriod.Monthly.getPeriodsPerYear());

        Loan loan = new Loan();
        loan.setTerms(terms);
        loan.setCharges(new HashSet<>());
        loan.setPayments(new HashSet<>());
        loan.setCloseDate(now.minusDays(1));

        LoanStatus loanStatus = new LoanStatus(loan);

        // The loan came to term two months ago: doesn't matter
        // The loan was closed yesterday: scheduled date is the period date before yesterday: 1 month ago

        TimePeriod paymentFrequency = TimePeriod.getTimePeriodWithPeriodCountOf(terms.getPaymentFrequency());
        LocalDate expResult = paymentFrequency.getDateFrom(now, -1);
        LocalDate result = loanStatus.getCurrentScheduledPaymentDate();

        assertEquals("Current scheduled payment for closed loan is before loan was closed.", expResult, result);

    }



}
