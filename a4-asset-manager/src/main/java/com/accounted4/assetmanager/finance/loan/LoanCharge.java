package com.accounted4.assetmanager.finance.loan;

import com.accounted4.assetmanager.AbstractEntity;
import java.time.LocalDate;
import javax.money.MonetaryAmount;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import lombok.Getter;
import lombok.Setter;
import org.javamoney.moneta.Money;

/**
 * Entity representing a Payment toward a loan
 *
 * @author gheinze
 */
@Getter @Setter
@Entity
public class LoanCharge extends AbstractEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "loan_id")
    private Loan loan;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "charge_type_id", nullable = false)
    private LoanChargeType loanChargeType;

    private String currency;
    private double amount;

    private LocalDate chargeDate;

    private String note;


    // Used to support presentation of the amount in the ui table
    // ui supports MoentaryAmount formatting, but Entity does not: it is
    // split into Number and Currency.
    public MonetaryAmount getDisplayAmount() {
        return Money.of(amount, currency);
    }


}
