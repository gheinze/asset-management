package com.accounted4.assetmanager.repository;

import com.accounted4.assetmanager.entity.PaymentDocumentType;
import com.accounted4.assetmanager.entity.PaymentDocumentStatus;
import com.accounted4.assetmanager.entity.LoanChargeType;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author gheinze
 */
public class LoanRepositoryImpl implements LoanRepositoryLov {

    @PersistenceContext
    private EntityManager em;



    private static final String JPQL_FOR_CHARGE_TYPES = "SELECT t FROM LoanChargeType t ORDER BY sort_order";

    @Override
    public List<LoanChargeType> getAllLoanChargeTypes() {
        return em
                .createQuery(JPQL_FOR_CHARGE_TYPES, LoanChargeType.class)
                .getResultList()
                ;
    }



    private static final String JPQL_FOR_DEFAULT_CHARGE_TYPE = "SELECT t FROM LoanChargeType t WHERE charge_type = 'Interest'";

    @Override
    public LoanChargeType getDefaultChargeType() {
        return em
                .createQuery(JPQL_FOR_DEFAULT_CHARGE_TYPE, LoanChargeType.class)
                .getSingleResult()
                ;
    }




    private static final String JPQL_FOR_PAYMENT_TYPES = "SELECT t FROM PaymentDocumentType t";

    @Override
    public List<PaymentDocumentType> getAllPaymentDocumentTypes() {
        return em
                .createQuery(JPQL_FOR_PAYMENT_TYPES, PaymentDocumentType.class)
                .getResultList()
                ;
    }


    private static final String JPQL_FOR_PAYMENT_STATUS = "SELECT s FROM PaymentDocumentStatus s";

    @Override
    public List<PaymentDocumentStatus> getAllPaymentDocumentStatus() {
        return em
                .createQuery(JPQL_FOR_PAYMENT_STATUS, PaymentDocumentStatus.class)
                .getResultList();
    }



    private static final String JPQL_FOR_DEFAULT_PAYMENT_TYPE = "SELECT t FROM PaymentDocumentType t WHERE document_type = 'Cheque'";

    @Override
    public PaymentDocumentType getDefaultPaymentDocumentType() {
        return em
                .createQuery(JPQL_FOR_DEFAULT_PAYMENT_TYPE, PaymentDocumentType.class)
                .getSingleResult()
                ;
    }


    private static final String JPQL_FOR_DEFAULT_PAYMENT_STATUS = "SELECT s FROM PaymentDocumentStatus s WHERE document_status = 'On File'";

    @Override
    public PaymentDocumentStatus getDefaultPaymentDocumentStatus() {
        return em
                .createQuery(JPQL_FOR_DEFAULT_PAYMENT_STATUS, PaymentDocumentStatus.class)
                .getSingleResult()
                ;
    }

}
