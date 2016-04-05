package com.accounted4.assetmanager.ui.loan.status;

import com.accounted4.finance.loan.AmortizationAttributes;
import com.accounted4.finance.loan.AmortizationCalculator;
import com.accounted4.finance.loan.ScheduledPayment;
import java.time.LocalDate;
import javax.money.MonetaryAmount;

/**
 *
 * @author gheinze
 */
public class LoanStatusScheduledPaymentLineItem extends LoanStatusLineItem {

    private final LocalDate nextScheduledPaymentDate;
    private final AmortizationAttributes amAttrs;


    public LoanStatusScheduledPaymentLineItem(
             ScheduledPayment scheduledPayment
            ,LocalDate nextScheduledPaymentDate
            ,AmortizationAttributes amAttrs) {

        this.nextScheduledPaymentDate = nextScheduledPaymentDate;
        this.amAttrs = amAttrs;

        setDate(scheduledPayment.getPaymentDate());
        setScheduledInterest(scheduledPayment.getInterest());
        setScheduledPrincipal(scheduledPayment.getPrincipal());
        setScheduledBalance(scheduledPayment.getBalance());

    }


    @Override
    public Accumulator aggregate(Accumulator accumulator) {

        // Running balances if they are not into the future
        if (!getDate().isAfter(nextScheduledPaymentDate)) {
            amAttrs.setLoanAmount(accumulator.getBalance());
            MonetaryAmount periodInterest = AmortizationCalculator.getPeriodInterest(amAttrs);
            setTransaction(periodInterest);
            accumulator.addToBalance(periodInterest);
            setBalance(accumulator.getBalance());
            setFees(accumulator.getFeeBalance());
            setType("Period Interest");
            setNote("Scheduled interest charge");
        }

        return accumulator;

    }

}
