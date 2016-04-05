package com.accounted4.assetmanager.ui.loan;

import com.accounted4.assetmanager.entity.LoanPayment;
import com.accounted4.assetmanager.entity.LoanChargeType;
import com.accounted4.assetmanager.entity.LoanCharge;
import com.accounted4.assetmanager.entity.Loan;
import com.accounted4.assetmanager.entity.Cheque;
import com.accounted4.finance.loan.AmortizationAttributes;
import com.accounted4.finance.loan.AmortizationCalculator;
import com.accounted4.finance.loan.ScheduledPayment;
import com.accounted4.finance.loan.TimePeriod;
import java.math.BigDecimal;
import java.time.LocalDate;
import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.MONTHS;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import javax.money.MonetaryAmount;
import lombok.Getter;
import org.javamoney.moneta.Money;

/**
 * Object to hold ordered transaction history of a loan.
 *
 * There are 3 transaction types:
 *  o Payments (entered on the Payments Tab)
 *  o Charges (entered on the Charges Tab)
 *  o Scheduled interest charges (automatically computed)
 *
 * A running total of these transactions is generated from the
 * beginning of the mortgage until a specified time. The time
 * is constrained to:
 *  o the next payment date (for the status report)
 *  o the last payment date (for the deposit form)
 *
 * All Payments and Charges, including future dated, will be listed
 *
 * Scheduled interest charges are only listed until the specified date.
 * That is, future dated interest charges beyond the specified date
 * are not shown. Also, scheduled interest charges after the loan
 * has been "closed" are not computed.
 *
 * next scheduled payment date is >= now (if mortgage not closed)
 * current scheduled payment is <= now (if mortgage not closed)
 *
 * A transaction list of line items in the following format is generated:
 *
 *          Expected                              Actual
 *   Date   Payment Interest Principal Balance    Payment Interest Principal Balance Fees Note
 *
 *
 * @author gheinze
 */
public class LoanStatus {

    private static final String FORWARDED_FUNDS_NOTE = "Forwarded funds";


    private final Loan loan;
    private final AmortizationAttributes amAttr;

    private final LocalDate now = LocalDate.now();

    private final ArrayList<LoanStatusLineItem> transactions = new ArrayList<>();

    @Getter private final LocalDate currentScheduledPaymentDate;
    @Getter private final LocalDate nextScheduledPaymentDate;
    private LoanStatusLineItem nextScheduledPaymentLineItem;

    private MonetaryAmount balance;
    private MonetaryAmount feeBalance;


    public LoanStatus(Loan loan) {
        this.loan = loan;
        this.amAttr = loan.getTerms().getAsAmAttributes();
        nextScheduledPaymentDate = calculateNextScheduledPaymentDate();
        currentScheduledPaymentDate = calculateLastScheduledPaymentDate(nextScheduledPaymentDate);
        populateTransactionList();
    }


    private LocalDate calculateNextScheduledPaymentDate() {

        TimePeriod paymentFrequency = TimePeriod.getTimePeriodWithPeriodCountOf(amAttr.getPaymentFrequency());
        LocalDate result = loan.getTerms().getStartDate();

        LocalDate interestAccrualUntil = now;

        if (loan.isClosed()) {
            interestAccrualUntil = paymentFrequency.getDateFrom(loan.getCloseDate(), -1);
            if (interestAccrualUntil.isAfter(now)) {
                interestAccrualUntil = now;
            }
        }

        while (result.isBefore(interestAccrualUntil)) {
            result = paymentFrequency.getDateFrom(result, 1);
        }
        return result;
    }


    private LocalDate calculateLastScheduledPaymentDate(LocalDate nextScheduledPayment) {
        return !nextScheduledPayment.isAfter(now) ?
                nextScheduledPayment :
                TimePeriod
                        .getTimePeriodWithPeriodCountOf(amAttr.getPaymentFrequency())
                        .getDateFrom(nextScheduledPayment, -1);
    }


    private void populateTransactionList() {

        // Key for sorting items: date (yyyy-mm-dd), type (scheduled interest payment, charges, payments), sequence #
        TreeMap<String, LoanStatusLineItem> transactionMap = new TreeMap<>();

        addOpeningBalanceTo(transactionMap);
        addScheduledPaymentsTo(transactionMap);
        addChargesTo(transactionMap);
        addPaymentsTo(transactionMap);
        createTransactionListWithRunningTotals(transactionMap);

    }


    private void addOpeningBalanceTo(Map<String, LoanStatusLineItem> map) {
        LoanCharge forwardedFunds = getOpeningCharge();
        LocalDate fundingDate = forwardedFunds.getChargeDate();
        String theKey = String.format("%04d-%02d-%02d  A %d",
                fundingDate.getYear(), fundingDate.getMonthValue(), fundingDate.getDayOfMonth(), forwardedFunds.getId());
        LoanStatusLineItem openingLineItem = convertChargeToLineItem(forwardedFunds);
        MonetaryAmount zero = openingLineItem.getTransaction().multiply(0L);
        openingLineItem.setScheduledBalance(openingLineItem.getTransaction());
        openingLineItem.setScheduledInterest(zero);
        openingLineItem.setScheduledPrincipal(zero);
        map.put(theKey, openingLineItem);
    }


    private void addScheduledPaymentsTo(Map<String, LoanStatusLineItem> map) {

        List<ScheduledPayment> scheduledPayments = AmortizationCalculator.generateSchedule(amAttr);

        scheduledPayments.stream()
                .filter(scheduledPayment -> !scheduledPayment.getPaymentDate().isAfter(nextScheduledPaymentDate))
                .forEachOrdered(scheduledPayment -> {
                    LocalDate paymentDate = scheduledPayment.getPaymentDate();
                    String key = String.format("%04d-%02d-%02d  A %d",
                            paymentDate.getYear(), paymentDate.getMonthValue(), paymentDate.getDayOfMonth(), scheduledPayment.getPaymentNumber());
                    map.put(key, convertScheduledPaymentToLineItem(scheduledPayment));
                }
        );

    }


    private void addChargesTo(Map<String, LoanStatusLineItem> map) {
        loan.getCharges().stream().forEach(charge -> {
            LocalDate chargeDate = charge.getChargeDate();
            String key = String.format("%04d-%02d-%02d  B %d",
                    chargeDate.getYear(), chargeDate.getMonthValue(), chargeDate.getDayOfMonth(), charge.getId());
            map.put(key, convertChargeToLineItem(charge));
        });
    }


    private void addPaymentsTo(Map<String, LoanStatusLineItem> map) {
        loan.getPayments().stream().forEach(payment -> {
            LocalDate depositDate = payment.getDepositDate();
            String key = String.format("%04d-%02d-%02d  C %d",
                    depositDate.getYear(), depositDate.getMonthValue(), depositDate.getDayOfMonth(), payment.getId());
            map.put(key, convertPaymentToLineItem(payment));
        });
    }


    private LoanCharge getOpeningCharge() {

        LoanCharge openingCharge = new LoanCharge();
        openingCharge.setLoan(loan);
        openingCharge.setLoanChargeType(LoanChargeType.getOtherCapitalizing());
        openingCharge.setCurrency(loan.getTerms().getLoanCurrency());
        openingCharge.setAmount(loan.getTerms().getLoanAmount());
        openingCharge.setChargeDate(loan.getTerms().getStartDate());
        openingCharge.setNote(FORWARDED_FUNDS_NOTE);

        return openingCharge;
    }


    private LoanStatusLineItem convertChargeToLineItem(LoanCharge charge) {

        LoanStatusLineItem lineItem = new LoanStatusLineItem();

        lineItem.setCharge(true);
        lineItem.setDate(charge.getChargeDate());
        lineItem.setTransaction(charge.getDisplayAmount());
        lineItem.setType(charge.getLoanChargeType().getChargeType());
        lineItem.setNote(charge.getNote());
        lineItem.setCapitalizing(charge.getLoanChargeType().isCapitalizing());

        return lineItem;
    }


    private LoanStatusLineItem convertPaymentToLineItem(LoanPayment payment) {

        LoanStatusLineItem lineItem = new LoanStatusLineItem();

        lineItem.setPayment(true);
        lineItem.setDate(payment.getDepositDate());
        lineItem.setTransaction(payment.getDisplayAmount().negate());
        lineItem.setType("Payment");
        lineItem.setNote(payment.getNote());

        return lineItem;
    }


    private LoanStatusLineItem convertScheduledPaymentToLineItem(ScheduledPayment scheduledPayment) {

        LoanStatusLineItem lineItem = new LoanStatusLineItem();

        lineItem.setScheduledPayment(true);
        lineItem.setDate(scheduledPayment.getPaymentDate());
        lineItem.setScheduledInterest(scheduledPayment.getInterest());
        lineItem.setScheduledPrincipal(scheduledPayment.getPrincipal());
        lineItem.setScheduledBalance(scheduledPayment.getBalance());

        return lineItem;

    }




    private void createTransactionListWithRunningTotals(Map<String, LoanStatusLineItem> map) {

        // Initialize running balances
        balance = Money.of(BigDecimal.ZERO, loan.getTerms().getLoanCurrency());
        feeBalance = balance.multiply(0L);

        map.values().stream().forEachOrdered((item) -> {
            aggregate(item);
            transactions.add(item);
        });

    }

    private void aggregate(LoanStatusLineItem item) {
        if (item.isCharge()) {
            aggregateCharge(item);
        } else if (item.isPayment()) {
            aggregatePayment(item);
        } else {
            aggregateScheduledPayment(item);
        }
    }


    private void aggregateCharge(LoanStatusLineItem item) {

        if (item.isCapitalizing()) {
            balance = balance.add(item.getTransaction());
        } else {
            feeBalance = feeBalance.add(item.getTransaction());
        }

        item.setBalance(balance);
        item.setFees(feeBalance);

    }


    private void aggregatePayment(LoanStatusLineItem item) {

        MonetaryAmount paymentAmount = item.getTransaction().negate();

        if (feeBalance.isPositive()) {
            if (paymentAmount.compareTo(feeBalance) < 0) {
                // Full payment is applied to fees
                feeBalance = feeBalance.subtract(paymentAmount);
            } else {
                // Part of the payment wipes out the fees, the rest is applied to principal
                balance = balance.subtract(paymentAmount.subtract(feeBalance));
                feeBalance = feeBalance.multiply(0L);
            }
        } else {
            // No fees to pay off, apply full payment to principal
            balance = balance.subtract(paymentAmount);
        }

        item.setBalance(balance);
        item.setFees(feeBalance);

    }

    private void aggregateScheduledPayment(LoanStatusLineItem item) {

        // Running balances if they are not into the future
        if (!item.getDate().isAfter(nextScheduledPaymentDate)) {
            AmortizationAttributes amAttrs = loan.getTerms().getAsAmAttributes();
            amAttrs.setLoanAmount(balance);
            MonetaryAmount periodInterest = AmortizationCalculator.getPeriodInterest(amAttrs);
            item.setTransaction(periodInterest);
            balance = balance.add(periodInterest);
            item.setBalance(balance);
            item.setFees(feeBalance);
            item.setType("Period Interest");
            item.setNote("Scheduled interest charge");
        }

        if (null == nextScheduledPaymentLineItem || item.getDate().isEqual(nextScheduledPaymentDate)) {
            nextScheduledPaymentLineItem = item;
        }


    }







    public List<LoanStatusLineItem> getOrderedLineItems() {
        return transactions;
    }


    public String getLoanName() {
        return loan.getLoanName();
    }

    public MonetaryAmount getRegularDue() {
        return amAttr.getRegularPayment();
    }

    public MonetaryAmount getActualDue() {
        return balance
                .subtract(nextScheduledPaymentLineItem.getScheduledBalance())
                .add(feeBalance)
                ;
    }

    public MonetaryAmount getBalance() {
        return balance;
    }


    public Optional<Cheque> getNextChequeOnFile() {
        return loan.getCheques().stream()
                .filter(c -> c.getDocumentStatus().getDocumentStatus().equalsIgnoreCase("On file"))
                .min( (c1, c2) -> { return c1.getPostDate().compareTo(c2.getPostDate()); } );
    }

    public Long getDaysToMaturity() {
        LocalDate maturityDate = amAttr.getStartDate().plus(amAttr.getTermInMonths(), MONTHS);
        return now.until(maturityDate, DAYS);
    }

    public MonetaryAmount getPerDiem() {
        return AmortizationCalculator.getPerDiem(balance, amAttr.getInterestRateAsPercent());
    }


    public MonetaryAmount getCurrentDue() {

        MonetaryAmount scheduleBalance = Money.of(BigDecimal.ZERO, loan.getTerms().getLoanCurrency()).multiply(0L);
        MonetaryAmount result = scheduleBalance.multiply(0L);

        for (LoanStatusLineItem transaction : transactions) {

            if (transaction.getDate().isAfter(currentScheduledPaymentDate)) {
                break;
            }

            if (null != transaction.getScheduledBalance()) {
                // Scheduled payment OR opening funds
                scheduleBalance = transaction.getScheduledBalance();
            }

            result = transaction.getBalance()
                    .subtract(scheduleBalance)
                    .add(transaction.getFees())
                    ;
        }

        return result;
    }

}
