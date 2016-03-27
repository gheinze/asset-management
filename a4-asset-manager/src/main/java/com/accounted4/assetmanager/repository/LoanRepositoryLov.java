package com.accounted4.assetmanager.repository;

import com.accounted4.assetmanager.entity.PaymentDocumentType;
import com.accounted4.assetmanager.entity.PaymentDocumentStatus;
import com.accounted4.assetmanager.entity.LoanChargeType;
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
