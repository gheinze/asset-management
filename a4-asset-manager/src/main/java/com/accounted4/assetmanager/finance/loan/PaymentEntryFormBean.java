package com.accounted4.assetmanager.finance.loan;

import com.accounted4.assetmanager.entity.Cheque;
import java.time.LocalDate;
import java.util.List;
import javax.money.MonetaryAmount;
import lombok.Getter;
import lombok.Setter;

/**
 * Backing bean for a PaymentEntryForm
 * @author gheinze
 */
@Getter @Setter
public class PaymentEntryFormBean {

    private boolean editMode;
    private List<Cheque> cheques;

    private Cheque cheque;
    private MonetaryAmount amount;
    private LocalDate depositDate;
    private String note;

}
