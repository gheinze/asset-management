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

    private static final String JPQL_FOR_PAYMENT_TYPES =
            "SELECT t FROM PaymentDocumentType t";

    private static final String JPQL_FOR_PAYMENT_STATUS =
            "SELECT s FROM PaymentDocumentStatus s";

    @Override
    public List<PaymentDocumentType> getAllPaymentDocumentTypes() {
        TypedQuery<PaymentDocumentType> paymentDocumentTypesQuery = em.createQuery(JPQL_FOR_PAYMENT_TYPES, PaymentDocumentType.class);
        return (List<PaymentDocumentType>)paymentDocumentTypesQuery.getResultList();
    }

    @Override
    public List<PaymentDocumentStatus> getAllPaymentDocumentStatus() {
        TypedQuery<PaymentDocumentStatus> paymentDocumentStatusQuery = em.createQuery(JPQL_FOR_PAYMENT_STATUS, PaymentDocumentStatus.class);
        return (List<PaymentDocumentStatus>)paymentDocumentStatusQuery.getResultList();
    }

}
