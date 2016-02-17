package com.accounted4.assetmanager.finance.loan;

import com.accounted4.finance.loan.AmortizationAttributes;
import com.accounted4.finance.loan.AmortizationCalculator;
import com.accounted4.finance.loan.ScheduledPayment;
import com.accounted4.finance.loan.TimePeriod;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import javax.money.MonetaryAmount;

/**
 * Object to hold ordered transaction history of mortgage.
 *
 * TODO: Add header informing of:
 *
    Next Payment Date:
    Regular Amount: (then remove scheduled amount col from table)
    Arears Amount:
    Next Available Cheque:

 *
        	Expected				Actual

    Date	Payment Interest Principal Balance    Payment Interest Principal Balance Fees Note

 *
 * @author gheinze
 */
public class LoanStatus {

    private final Loan loan;

    private final LocalDate now = LocalDate.now();
    private final ArrayList<LoanStatusLineItem> transactions = new ArrayList<>();

    private MonetaryAmount balance;
    private MonetaryAmount feeBalance;


    public LoanStatus(Loan loan) {
        this.loan = loan;
        populateScheduledPayments();
    }


    private void populateScheduledPayments() {

        // Key for sorting items: date (yyyy-mm-dd), type (1st scheduled, 2nd charges, 3rd payments), sequence #
        TreeMap<String, Object> orderedLineItems = new TreeMap<>();

        List<ScheduledPayment> scheduledPayments = AmortizationCalculator.generateSchedule(loan.getTerms().getAsAmAttributes());
        scheduledPayments.stream().forEach(p -> {
            String key = String.format("%04d-%02d-%02d  A %d",
                    p.getPaymentDate().getYear(), p.getPaymentDate().getMonthValue(), p.getPaymentDate().getDayOfMonth(), p.getPaymentNumber());
            orderedLineItems.put(key, p);
        });

        loan.getCharges().stream().forEach(c -> {
            String key = String.format("%04d-%02d-%02d  B %d",
                    c.getChargeDate().getYear(), c.getChargeDate().getMonthValue(), c.getChargeDate().getDayOfMonth(), c.getId());
            orderedLineItems.put(key, c);
        });

        loan.getPayments().stream().forEach(p -> {
            String key = String.format("%04d-%02d-%02d  C %d",
                    p.getDepositDate().getYear(), p.getDepositDate().getMonthValue(), p.getDepositDate().getDayOfMonth(), p.getId());
            orderedLineItems.put(key, p);
        });

        LoanStatusLineItem openingLineItem = getOpeningBalance();
        transactions.add(openingLineItem);

        balance = openingLineItem.getScheduledBalance();
        feeBalance = balance.multiply(0L);

        orderedLineItems.values().stream().forEachOrdered((item) -> {
            if (item instanceof LoanCharge) {
                transactions.add( getChargeLineItem( (LoanCharge)item ) );
            } else if (item instanceof LoanPayment) {
                transactions.add( getPaymentLineItem( (LoanPayment)item ) );
            } else {
                transactions.add( getScheduledLineItem( (ScheduledPayment)item ) );
            }
        });

    }


    private LoanStatusLineItem getOpeningBalance() {
        LoanStatusLineItem lineItem = new LoanStatusLineItem();
        lineItem.setDate(loan.getTerms().getStartDate());
        lineItem.setScheduledBalance(loan.getTerms().getAsAmAttributes().getLoanAmount());
        lineItem.setBalance(lineItem.getScheduledBalance());
        return lineItem;
    }


    private LoanStatusLineItem getChargeLineItem(LoanCharge charge) {

        LoanStatusLineItem lineItem = new LoanStatusLineItem();

        lineItem.setDate(charge.getChargeDate());
        lineItem.setTransaction(charge.getDisplayAmount());
        lineItem.setType(charge.getLoanChargeType().getChargeType());
        lineItem.setNote(charge.getNote());

        if (charge.getLoanChargeType().isCapitalizing()) {
            balance = balance.add(charge.getDisplayAmount());
        } else {
            feeBalance = feeBalance.add(charge.getDisplayAmount());
        }

        lineItem.setBalance(balance);
        lineItem.setFees(feeBalance);

        return lineItem;
    }


    private LoanStatusLineItem getPaymentLineItem(LoanPayment payment) {

        // Payments first apply to fees, then to principal

        MonetaryAmount fullPayment =  payment.getDisplayAmount();

        LoanStatusLineItem lineItem = new LoanStatusLineItem();

        lineItem.setDate(payment.getDepositDate());
        lineItem.setTransaction(fullPayment.negate());
        lineItem.setType("Payment");
        lineItem.setNote(payment.getNote());

        if (feeBalance.isPositive()) {
            if (fullPayment.compareTo(feeBalance) < 0) {
                // Full payment is applied to fees
                feeBalance = feeBalance.subtract(fullPayment);
            } else {
                // Part of the payment wipes out the fees, the rest is applied to principal
                balance = balance.subtract(fullPayment.subtract(feeBalance));
                feeBalance = feeBalance.multiply(0L);
            }
        } else {
            // No fees to pay off, apply full payment to principal
            balance = balance.subtract(fullPayment);
        }

        lineItem.setBalance(balance);
        lineItem.setFees(feeBalance);

        return lineItem;
    }



    private LoanStatusLineItem getScheduledLineItem(ScheduledPayment scheduledPayment) {

        LoanStatusLineItem lineItem = new LoanStatusLineItem();

        lineItem.setDate(scheduledPayment.getPaymentDate());
        lineItem.setScheduledInterest(scheduledPayment.getInterest());
        lineItem.setScheduledPrincipal(scheduledPayment.getPrincipal());
        lineItem.setScheduledBalance(scheduledPayment.getBalance());

        if (!scheduledPayment.getPaymentDate().isAfter(now)) {
            AmortizationAttributes amAttrs = loan.getTerms().getAsAmAttributes();
            amAttrs.setLoanAmount(balance);
            MonetaryAmount periodInterest = AmortizationCalculator.getPeriodInterest(amAttrs);
            lineItem.setTransaction(periodInterest);
            balance = balance.add(periodInterest);
            lineItem.setBalance(balance);
            lineItem.setFees(feeBalance);
            lineItem.setType("Period Interest");
            lineItem.setNote("Scheduled interest charge");
        }

        return lineItem;

    }



    public LocalDate getNextScheduledPaymentDate() {

        LocalDate nextScheduledPaymentDate = loan.getTerms().getStartDate();
        TimePeriod paymentFrequency = TimePeriod.getTimePeriodWithPeriodCountOf(loan.getTerms().getPaymentFrequency());

        do {
            nextScheduledPaymentDate = paymentFrequency.getDateFrom(nextScheduledPaymentDate, 1L);
        } while (nextScheduledPaymentDate.compareTo(now) < 0);

        return nextScheduledPaymentDate;

    }

    public List<LoanStatusLineItem> getOrderedLineItems() {
        return transactions;
    }


}
