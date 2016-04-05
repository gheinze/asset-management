package com.accounted4.assetmanager.entity;

import java.time.LocalDate;
import java.util.Objects;
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

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 61 * hash + Objects.hashCode(this.loanChargeType);
        hash = 61 * hash + Objects.hashCode(this.currency);
        hash = 61 * hash + (int) (Double.doubleToLongBits(this.amount) ^ (Double.doubleToLongBits(this.amount) >>> 32));
        hash = 61 * hash + Objects.hashCode(this.chargeDate);
        hash = 61 * hash + Objects.hashCode(this.note);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final LoanCharge other = (LoanCharge) obj;
        if (Double.doubleToLongBits(this.amount) != Double.doubleToLongBits(other.amount)) {
            return false;
        }
        if (!Objects.equals(this.currency, other.currency)) {
            return false;
        }
        if (!Objects.equals(this.note, other.note)) {
            return false;
        }
        if (!Objects.equals(this.loanChargeType, other.loanChargeType)) {
            return false;
        }
        if (!Objects.equals(this.chargeDate, other.chargeDate)) {
            return false;
        }
        return true;
    }


    }
