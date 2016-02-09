package com.accounted4.assetmanager.finance.loan;

import java.util.List;

/**
 *
 * @author gheinze
 */
public interface LoanRepositoryLov {

    List<PaymentDocumentType> getAllPaymentDocumentTypes();
    List<PaymentDocumentStatus> getAllPaymentDocumentStatus();

}
