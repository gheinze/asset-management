package com.accounted4.assetmanager.ui.loan;

import com.accounted4.assetmanager.entity.Cheque;
import com.accounted4.assetmanager.entity.Loan;
import java.time.LocalDate;
import javax.money.MonetaryAmount;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author gheinze
 */
@Getter
@Setter
public class DepositLineItem {

    private Loan loan;
    private boolean selected;
    private String loanName;
    private MonetaryAmount regularPayment;
    private LocalDate asOf;
    private MonetaryAmount currentDue;
    private Cheque cheque;

}
