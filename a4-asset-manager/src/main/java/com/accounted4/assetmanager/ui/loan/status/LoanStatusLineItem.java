package com.accounted4.assetmanager.ui.loan.status;

import java.time.LocalDate;
import javax.money.MonetaryAmount;
import lombok.Getter;
import lombok.Setter;

/**
 * Bean representing a transaction item in the history of transactions presented by the LoanStatus.
 * Could be private static nested class of LoanStatus.
 * @author gheinze
 */
@Getter
@Setter
public abstract class LoanStatusLineItem {

    private LocalDate date;
    private MonetaryAmount scheduledInterest;
    private MonetaryAmount scheduledPrincipal;
    private MonetaryAmount scheduledBalance;
    private MonetaryAmount transaction;
    private MonetaryAmount balance;
    private MonetaryAmount fees;
    private String type;
    private String note;


    /**
     * A line item has running balances. This method should:
     *   1. Update the running balances of the accumulator according this line item
     *      (i.e. if it is an interest charge, the balance should be increased)
     *   2. Apply the accumulator balances to the summary fields of this item (balance and fees).
     *
     * @param accumulator
     * @return
     */
    public abstract Accumulator aggregate(Accumulator accumulator);
    

    public MonetaryAmount getScheduledAmount() {
        if (null != scheduledInterest) {
            return null == scheduledPrincipal ? scheduledInterest : scheduledInterest.add(scheduledPrincipal);
        }
        return null == scheduledPrincipal ? null : scheduledPrincipal;
    }

}
