package com.accounted4.assetmanager.finance.loan;

import java.util.List;

/**
 *
 * @author gheinze
 */
public interface LoanRepositoryLov {

    List<LoanChargeType> getAllLoanChargeTypes();
    LoanChargeType getDefaultChargeType();

    List<PaymentDocumentType> getAllPaymentDocumentTypes();
    List<PaymentDocumentStatus> getAllPaymentDocumentStatus();

    PaymentDocumentType getDefaultPaymentDocumentType();
    PaymentDocumentStatus getDefaultPaymentDocumentStatus();

}
