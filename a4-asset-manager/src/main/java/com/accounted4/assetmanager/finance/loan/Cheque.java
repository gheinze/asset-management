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

    public String getCheque() {
        return this.toString();
    }

    public MonetaryAmount getDisplayAmount() {
        return Money.of(amount, loan.getTerms().getLoanCurrency());
    }

    public void setDisplayAmount(MonetaryAmount displayAmount) {
        this.amount = displayAmount.getNumber().doubleValue();
    }

    @Override
    public String toString() {
        return (null != reference ? reference : "") +
                " " +
                postDate + " " +
                amount + " " +
                documentStatus +
                (null != note ? " " + note : "")
                ;
    }


}
