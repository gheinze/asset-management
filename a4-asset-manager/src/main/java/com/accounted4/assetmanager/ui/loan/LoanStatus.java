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
import java.util.Optional;
import java.util.TreeMap;
import javax.money.MonetaryAmount;
import lombok.Getter;
import org.javamoney.moneta.Money;

/**
 * Object to hold ordered transaction history of mortgage.
 *
 *       	Expected				Actual
 *
 *   Date	Payment Interest Principal Balance    Payment Interest Principal Balance Fees Note
 *
 *
 * @author gheinze
 */
public class LoanStatus {

    private final Loan loan;
    private final AmortizationAttributes amAttr;

    private final LocalDate now = LocalDate.now();

    private final ArrayList<LoanStatusLineItem> transactions = new ArrayList<>();

    @Getter private final LocalDate nextScheduledPaymentDate;
    private LoanStatusLineItem nextScheduledPaymentLineItem;

    private MonetaryAmount balance;
    private MonetaryAmount feeBalance;


    public LoanStatus(Loan loan) {
        this.loan = loan;
        this.amAttr = loan.getTerms().getAsAmAttributes();
        nextScheduledPaymentDate = calculateNextScheduledPaymentDate();
        populateScheduledPayments();
    }


    private LocalDate calculateNextScheduledPaymentDate() {

        TimePeriod paymentFrequency = TimePeriod.getTimePeriodWithPeriodCountOf(amAttr.getPaymentFrequency());
        LocalDate result = paymentFrequency.getDateFrom(loan.getTerms().getStartDate(), 1);

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


    private void populateScheduledPayments() {

        // Key for sorting items: date (yyyy-mm-dd), type (scheduled interest payment, charges, payments), sequence #
        TreeMap<String, Object> orderedLineItems = new TreeMap<>();

        List<ScheduledPayment> scheduledPayments = AmortizationCalculator.generateSchedule(amAttr);

        LoanCharge forwardedFunds = getOpeningCharge();
        String theKey = String.format("%04d-%02d-%02d  A %d",
                forwardedFunds.getChargeDate().getYear(), forwardedFunds.getChargeDate().getMonthValue(), forwardedFunds.getChargeDate().getDayOfMonth(), forwardedFunds.getId());
        orderedLineItems.put(theKey, forwardedFunds);

        scheduledPayments.stream()
                .filter(p -> !p.getPaymentDate().isAfter(nextScheduledPaymentDate))
                .forEachOrdered(p -> {
                    String key = String.format("%04d-%02d-%02d  A %d",
                            p.getPaymentDate().getYear(), p.getPaymentDate().getMonthValue(), p.getPaymentDate().getDayOfMonth(), p.getPaymentNumber());
                    orderedLineItems.put(key, p);
                }
        );

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


        balance = Money.of(BigDecimal.ZERO, loan.getTerms().getLoanCurrency());
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


    private LoanCharge getOpeningCharge() {

        LoanCharge openingCharge = new LoanCharge();
        openingCharge.setLoan(loan);
        openingCharge.setLoanChargeType(LoanChargeType.getOtherCapitalizing());
        openingCharge.setCurrency(loan.getTerms().getLoanCurrency());
        openingCharge.setAmount(loan.getTerms().getLoanAmount());
        openingCharge.setChargeDate(loan.getTerms().getStartDate());
        openingCharge.setNote("Forwarded funds");

        return openingCharge;
    }


    private LoanStatusLineItem getChargeLineItem(LoanCharge charge) {

        LoanStatusLineItem lineItem = new LoanStatusLineItem();

        lineItem.setDate(charge.getChargeDate());
        lineItem.setTransaction(charge.getDisplayAmount());
        lineItem.setType(charge.getLoanChargeType().getChargeType());
        lineItem.setNote(charge.getNote());
        lineItem.setCapitalizing(charge.getLoanChargeType().isCapitalizing());

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

        if (!scheduledPayment.getPaymentDate().isAfter(nextScheduledPaymentDate)) {
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

        if (null == nextScheduledPaymentLineItem || scheduledPayment.getPaymentDate().isEqual(nextScheduledPaymentDate)) {
            nextScheduledPaymentLineItem = lineItem;
        }

        return lineItem;

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


    public String getNextChequeOnFile() {
        Optional<Cheque> min = loan.getCheques().stream()
                .filter(c -> c.getDocumentStatus().getDocumentStatus().equalsIgnoreCase("On file"))
                .min( (c1, c2) -> { return c1.getPostDate().compareTo(c2.getPostDate()); } );
        if (min.isPresent()) {
            return min.get().toString();
        }
        return "No cheque on file";
    }

    public Long getDaysToMaturity() {
        LocalDate maturityDate = amAttr.getStartDate().plus(amAttr.getTermInMonths(), MONTHS);
        return now.until(maturityDate, DAYS);
    }

    public MonetaryAmount getPerDiem() {
        return AmortizationCalculator.getPerDiem(balance, amAttr.getInterestRateAsPercent());
    }

}
