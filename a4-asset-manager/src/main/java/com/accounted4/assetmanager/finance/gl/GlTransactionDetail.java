package com.accounted4.assetmanager.finance.gl;

import com.accounted4.assetmanager.AbstractEntity;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author gheinze
 */
@Getter @Setter
@Entity
public class GlTransactionDetail extends AbstractEntity {

    @ManyToOne
    @JoinColumn(name = "transaction_id")
    private GlTransaction glTransaction;

    private int sortOrder;

    @OneToOne
    @JoinColumn(name = "gl_account_id")
    private GlAccount glAccount;

    private String debitCredit;
    private double amount;
    private String description;

}
