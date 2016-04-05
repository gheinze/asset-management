package com.accounted4.assetmanager.ui.loan.status;

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

    private Accumulator balances;


    public LoanStatus(Loan loan) {
        this.loan = loan;
        this.amAttr = loan.getTerms().getAsAmAttributes();
        nextScheduledPaymentDate = calculateNextScheduledPaymentDate();
        currentScheduledPaymentDate = calculateLastScheduledPaymentDate(nextScheduledPaymentDate);
        populateTransactionList();
    }


    /*
     * next scheduled payment is >= today, unless the loan closed earlier
    */
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


    /*
     * current scheduled payment is <= today, unless the loan closed earlier
    */
    private LocalDate calculateLastScheduledPaymentDate(LocalDate nextScheduledPayment) {
        return !nextScheduledPayment.isAfter(now) ?
                nextScheduledPayment :
                TimePeriod
                        .getTimePeriodWithPeriodCountOf(amAttr.getPaymentFrequency())
                        .getDateFrom(nextScheduledPayment, -1);
    }


    /*
     * Add all charges, payments, and interest to a map in order to apply an order to the transactions,
     * then convert the map to an ordered list of transactions.
    */
    private void populateTransactionList() {

        // Key for sorting items: date (yyyy-mm-dd), type (scheduled interest payment, charges, payments), sequence #
        TreeMap<String, LoanStatusLineItem> transactionMap = new TreeMap<>();

        addOpeningBalanceTo(transactionMap);
        addScheduledPaymentsToMap(transactionMap);
        addChargesToMap(transactionMap);
        addPaymentsToMap(transactionMap);
        createTransactionListWithRunningTotals(transactionMap);

    }


    /*
     * This is a pseudo charge, but makes the report look better to show the opening balance.
    */
    private void addOpeningBalanceTo(Map<String, LoanStatusLineItem> map) {
        LoanCharge forwardedFunds = getOpeningCharge();
        LocalDate fundingDate = forwardedFunds.getChargeDate();
        String theKey = String.format("%04d-%02d-%02d  A %d",
                fundingDate.getYear(), fundingDate.getMonthValue(), fundingDate.getDayOfMonth(), forwardedFunds.getId());
        LoanStatusLineItem openingLineItem = new LoanStatusChargeLineItem(forwardedFunds);
        MonetaryAmount zero = openingLineItem.getTransaction().multiply(0L);
        openingLineItem.setScheduledBalance(openingLineItem.getTransaction());
        openingLineItem.setScheduledInterest(zero);
        openingLineItem.setScheduledPrincipal(zero);
        map.put(theKey, openingLineItem);
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


    private void addScheduledPaymentsToMap(Map<String, LoanStatusLineItem> map) {

        List<ScheduledPayment> scheduledPayments = AmortizationCalculator.generateSchedule(amAttr);

        scheduledPayments.stream()
                .filter(scheduledPayment -> !scheduledPayment.getPaymentDate().isAfter(nextScheduledPaymentDate))
                .forEachOrdered(scheduledPayment -> {
                    LocalDate paymentDate = scheduledPayment.getPaymentDate();
                    String key = String.format("%04d-%02d-%02d  A %d",
                            paymentDate.getYear(), paymentDate.getMonthValue(), paymentDate.getDayOfMonth(), scheduledPayment.getPaymentNumber());
                    LoanStatusScheduledPaymentLineItem item =
                            new LoanStatusScheduledPaymentLineItem(scheduledPayment, nextScheduledPaymentDate, amAttr);
                    map.put(key, item);
                    if (null == nextScheduledPaymentLineItem || item.getDate().isEqual(nextScheduledPaymentDate)) {
                        nextScheduledPaymentLineItem = item;
                    }
                }
        );

    }


    private void addChargesToMap(Map<String, LoanStatusLineItem> map) {
        loan.getCharges().stream().forEach(charge -> {
            LocalDate chargeDate = charge.getChargeDate();
            String key = String.format("%04d-%02d-%02d  B %d",
                    chargeDate.getYear(), chargeDate.getMonthValue(), chargeDate.getDayOfMonth(), charge.getId());
            map.put(key, new LoanStatusChargeLineItem(charge));
        });
    }


    private void addPaymentsToMap(Map<String, LoanStatusLineItem> map) {
        loan.getPayments().stream().forEach(payment -> {
            LocalDate depositDate = payment.getDepositDate();
            String key = String.format("%04d-%02d-%02d  C %d",
                    depositDate.getYear(), depositDate.getMonthValue(), depositDate.getDayOfMonth(), payment.getId());
            map.put(key, new LoanStatusPaymentLineItem(payment));
        });
    }


    /*
     * Convert the ordered map of transactions into an ordered list of transactions
     * (key can be thrown away at now since it was just used for sorting purposes)
     * and update the running balance fields on each transaction as we go. We can
     * only do this aggregation now, when we have all the items in order.
    */
    private void createTransactionListWithRunningTotals(Map<String, LoanStatusLineItem> map) {

        // Initialize running balances
        MonetaryAmount zero = Money.of(BigDecimal.ZERO, loan.getTerms().getLoanCurrency()).multiply(0L);
        balances = new Accumulator(zero, zero);

        map.values().stream().forEachOrdered((item) -> {
            item.aggregate(balances);
            transactions.add(item);
        });

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


    /**
     * As of next scheduled payment date (>= today)
     *
     * @return
     */
    public MonetaryAmount getActualDue() {
        return balances
                .getBalance()
                .subtract(nextScheduledPaymentLineItem.getScheduledBalance())
                .add(balances.getFeeBalance())
                ;
    }

    /**
     * As of next scheduled payment date (>= today)
     *
     * @return
     */
    public MonetaryAmount getBalance() {
        return balances.getBalance();
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

    /**
     * As of next scheduled payment date (>= today)
     *
     * @return
     */
    public MonetaryAmount getPerDiem() {
        return AmortizationCalculator.getPerDiem(balances.getBalance(), amAttr.getInterestRateAsPercent());
    }


    /**
     * As of the last scheduled payment date (<= today, used for Deposit form) rather than as of the next scheduled
     * payment date (>= tpday, used by status report)).
     *
     * @return
     */
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
