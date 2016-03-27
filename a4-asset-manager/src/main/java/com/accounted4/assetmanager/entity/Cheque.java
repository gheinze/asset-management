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
 * Entity representing a Payment Document (cheque, money order, draft, ...)
 *
 * @author gheinze
 */
@Getter @Setter
@Entity
public class Cheque extends AbstractEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "loan_id")
    private Loan loan;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "document_type_id", nullable = false, updatable = false)
    private PaymentDocumentType documentType;

    private LocalDate postDate;
    private String currency;
    private double amount;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "document_status_id", nullable = false, updatable = false)
    private PaymentDocumentStatus documentStatus;

    private String reference;
    private String note;


    // Used to support presentation of the amount in the ui cheque table
    // ui supports MoentaryAmount formatting, but Entity does not: it is
    // split into Number and Currency.
    public MonetaryAmount getDisplayAmount() {
        return Money.of(amount, loan.getTerms().getLoanCurrency());
    }


    @Override
    public String toString() {
        return  String.format("%04d-%02d-%02d  $%(,.2f  %s  %s",
                postDate.getYear(), postDate.getMonthValue(), postDate.getDayOfMonth(),
                amount,
                (null != reference ? reference : ""),
                (null != note ? " " + note : "")
        );
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 37 * hash + Objects.hashCode(this.documentType);
        hash = 37 * hash + Objects.hashCode(this.postDate);
        hash = 37 * hash + Objects.hashCode(this.currency);
        hash = 37 * hash + (int) (Double.doubleToLongBits(this.amount) ^ (Double.doubleToLongBits(this.amount) >>> 32));
        hash = 37 * hash + Objects.hashCode(this.reference);
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
        final Cheque other = (Cheque) obj;
        if (Double.doubleToLongBits(this.amount) != Double.doubleToLongBits(other.amount)) {
            return false;
        }
        if (!Objects.equals(this.currency, other.currency)) {
            return false;
        }
        if (!Objects.equals(this.reference, other.reference)) {
            return false;
        }
        if (!Objects.equals(this.documentType, other.documentType)) {
            return false;
        }
        if (!Objects.equals(this.postDate, other.postDate)) {
            return false;
        }
        return true;
    }


}
