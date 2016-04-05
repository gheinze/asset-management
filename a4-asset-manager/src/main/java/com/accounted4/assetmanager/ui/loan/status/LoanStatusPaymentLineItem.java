package com.accounted4.assetmanager.ui.loan.status;

import com.accounted4.assetmanager.entity.LoanPayment;
import javax.money.MonetaryAmount;

/**
 *
 * @author gheinze
 */
public class LoanStatusPaymentLineItem extends LoanStatusLineItem {

    public LoanStatusPaymentLineItem(LoanPayment payment) {
        setDate(payment.getDepositDate());
        setTransaction(payment.getDisplayAmount().negate());
        setType("Payment");
        setNote(payment.getNote());
    }



    @Override
    public Accumulator aggregate(Accumulator accumulator) {

        MonetaryAmount paymentAmount = getTransaction().negate();

        if (accumulator.getFeeBalance().isPositive()) {
            if (paymentAmount.compareTo(accumulator.getFeeBalance()) < 0) {
                // Full payment is applied to fees
                accumulator.subtractFromFeeBalance(paymentAmount);
            } else {
                // Part of the payment wipes out the fees, the rest is applied to principal
                accumulator.subtractFromBalance(paymentAmount.subtract(accumulator.getFeeBalance()));
                accumulator.setFeeBalance(accumulator.getFeeBalance().multiply(0L));
            }
        } else {
            // No fees to pay off, apply full payment to principal
            accumulator.subtractFromBalance(paymentAmount);
        }

        setBalance(accumulator.getBalance());
        setFees(accumulator.getFeeBalance());

        return accumulator;

    }

}
