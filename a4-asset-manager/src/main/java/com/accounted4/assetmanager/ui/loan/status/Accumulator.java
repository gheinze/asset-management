package com.accounted4.assetmanager.ui.loan.status;

import javax.money.MonetaryAmount;
import lombok.Data;

/**
 * An object for keeping running balances of the Loan status report when iterating
 * through the ordered list of transactions.
 *
 * There are two balances each line item may impact:
 *   o balance (i.e. the principal owing)
 *   o fee balance (non-capitalizing charges tallied separately from the principal)
 * 
 * @author gheinze
 */
@Data
public class Accumulator {

    private MonetaryAmount balance;
    private MonetaryAmount feeBalance;

    public Accumulator(MonetaryAmount balance, MonetaryAmount feeBalance) {
        this.balance = balance;
        this.feeBalance = feeBalance;
    }


    public void addToBalance(MonetaryAmount amount) {
        balance = balance.add(amount);
    }

    public void addToFeeBalance(MonetaryAmount amount) {
        feeBalance = feeBalance.add(amount);
    }


    public void subtractFromBalance(MonetaryAmount amount) {
        balance = balance.subtract(amount);
    }

    public void subtractFromFeeBalance(MonetaryAmount amount) {
        feeBalance = feeBalance.subtract(amount);
    }


}
