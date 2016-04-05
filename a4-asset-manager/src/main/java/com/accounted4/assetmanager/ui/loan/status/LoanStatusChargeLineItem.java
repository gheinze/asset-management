package com.accounted4.assetmanager.ui.loan.status;

import com.accounted4.assetmanager.entity.LoanCharge;
import lombok.Getter;

/**
 *
 * @author gheinze
 */
public class LoanStatusChargeLineItem extends LoanStatusLineItem {

    @Getter private final boolean capitalizing;

    public LoanStatusChargeLineItem(LoanCharge charge) {
        setDate(charge.getChargeDate());
        setTransaction(charge.getDisplayAmount());
        setType(charge.getLoanChargeType().getChargeType());
        setNote(charge.getNote());
        capitalizing = charge.getLoanChargeType().isCapitalizing();
    }


    @Override
    public Accumulator aggregate(Accumulator accumulator) {

        if (capitalizing) {
            accumulator.addToBalance(getTransaction());
        } else {
            accumulator.addToFeeBalance(getTransaction());
        }

        setBalance(accumulator.getBalance());
        setFees(accumulator.getFeeBalance());

        return accumulator;

    }

}
