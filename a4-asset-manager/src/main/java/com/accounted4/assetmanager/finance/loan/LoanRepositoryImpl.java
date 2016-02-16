package com.accounted4.assetmanager.finance.loan;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

/**
 *
 * @author gheinze
 */
public class LoanRepositoryImpl implements LoanRepositoryLov {

    @PersistenceContext
    private EntityManager em;

    

    private static final String JPQL_FOR_CHARGE_TYPES = "SELECT t FROM LoanChargeType t";

    @Override
    public List<LoanChargeType> getAllLoanChargeTypes() {
        TypedQuery<LoanChargeType> chargeTypesQuery = em.createQuery(JPQL_FOR_CHARGE_TYPES, LoanChargeType.class);
        return (List<LoanChargeType>)chargeTypesQuery.getResultList();
    }



    private static final String JPQL_FOR_DEFAULT_CHARGE_TYPE = "SELECT t FROM LoanChargeType t WHERE charge_type = 'Interest'";

    @Override
    public LoanChargeType getDefaultChargeType() {
        TypedQuery<LoanChargeType> defaultChargeTypeQuery = em.createQuery(JPQL_FOR_DEFAULT_CHARGE_TYPE, LoanChargeType.class);
        return defaultChargeTypeQuery.getSingleResult();
    }




    private static final String JPQL_FOR_PAYMENT_TYPES = "SELECT t FROM PaymentDocumentType t";

    @Override
    public List<PaymentDocumentType> getAllPaymentDocumentTypes() {
        TypedQuery<PaymentDocumentType> paymentDocumentTypesQuery = em.createQuery(JPQL_FOR_PAYMENT_TYPES, PaymentDocumentType.class);
        return (List<PaymentDocumentType>)paymentDocumentTypesQuery.getResultList();
    }


    private static final String JPQL_FOR_PAYMENT_STATUS = "SELECT s FROM PaymentDocumentStatus s";

    @Override
    public List<PaymentDocumentStatus> getAllPaymentDocumentStatus() {
        TypedQuery<PaymentDocumentStatus> paymentDocumentStatusQuery = em.createQuery(JPQL_FOR_PAYMENT_STATUS, PaymentDocumentStatus.class);
        return (List<PaymentDocumentStatus>)paymentDocumentStatusQuery.getResultList();
    }



    private static final String JPQL_FOR_DEFAULT_PAYMENT_TYPE = "SELECT t FROM PaymentDocumentType t WHERE document_type = 'Cheque'";

    @Override
    public PaymentDocumentType getDefaultPaymentDocumentType() {
        TypedQuery<PaymentDocumentType> defaultPaymentDocumentTypeQuery = em.createQuery(JPQL_FOR_DEFAULT_PAYMENT_TYPE, PaymentDocumentType.class);
        return defaultPaymentDocumentTypeQuery.getSingleResult();
    }


    private static final String JPQL_FOR_DEFAULT_PAYMENT_STATUS = "SELECT s FROM PaymentDocumentStatus s WHERE document_status = 'On File'";

    @Override
    public PaymentDocumentStatus getDefaultPaymentDocumentStatus() {
        TypedQuery<PaymentDocumentStatus> defaultPaymentDocumentStatusQuery = em.createQuery(JPQL_FOR_DEFAULT_PAYMENT_STATUS, PaymentDocumentStatus.class);
        return defaultPaymentDocumentStatusQuery.getSingleResult();
    }

}
