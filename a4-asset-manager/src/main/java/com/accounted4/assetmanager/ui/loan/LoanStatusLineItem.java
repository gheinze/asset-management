package com.accounted4.assetmanager.ui.loan;

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
public class LoanStatusLineItem {

    private LocalDate date;
    private MonetaryAmount scheduledInterest;
    private MonetaryAmount scheduledPrincipal;
    private MonetaryAmount scheduledBalance;
    private MonetaryAmount transaction;
    private MonetaryAmount balance;
    private MonetaryAmount fees;
    private String type;
    private String note;
    private boolean capitalizing;

    private boolean charge;
    private boolean payment;
    private boolean scheduledPayment;

    public MonetaryAmount getScheduledAmount() {
        if (null != scheduledInterest) {
            return null == scheduledPrincipal ? scheduledInterest : scheduledInterest.add(scheduledPrincipal);
        }
        return null == scheduledPrincipal ? null : scheduledPrincipal;
    }

}
