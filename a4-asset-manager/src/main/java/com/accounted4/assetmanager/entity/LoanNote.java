package com.accounted4.assetmanager.entity;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author gheinze
 */
@Getter @Setter
@Entity
public class LoanNote extends AbstractEntity {

    private static final long serialVersionUID = 1L;

    @OneToOne
    @JoinColumn(name="loan_id")
    private Loan loan;
    private String note;

}
