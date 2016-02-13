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


}
