package com.accounted4.assetmanager.finance.loan;

import com.accounted4.assetmanager.entity.PaymentDocumentType;
import com.accounted4.assetmanager.entity.PaymentDocumentStatus;
import java.time.LocalDate;
import javax.money.MonetaryAmount;
import lombok.Getter;
import lombok.Setter;

/**
 * Backing bean for a ChequeEntryForm
 * @author gheinze
 */
@Getter @Setter
public class ChequeEntryFormBean {

    private PaymentDocumentType documentType;
    private LocalDate postDate;
    private MonetaryAmount amount;
    private PaymentDocumentStatus documentStatus;
    private int batch = 1;
    private String reference;
    private String note;

    private boolean batchEntryEnabled;

}
