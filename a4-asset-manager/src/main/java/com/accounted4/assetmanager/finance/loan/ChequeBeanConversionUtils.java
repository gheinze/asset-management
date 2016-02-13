package com.accounted4.assetmanager.finance.loan;

import com.accounted4.finance.loan.TimePeriod;
import java.util.ArrayList;
import java.util.List;
import org.javamoney.moneta.Money;

/**
 * Mapping utilities between Cheque Entity bean and UI Cheque Entry Form backing bean
 * @author gheinze
 */
public class ChequeBeanConversionUtils {


    public static ChequeEntryFormBean getChequeEntryFormBean(Cheque cheque) {
        ChequeEntryFormBean bean = new ChequeEntryFormBean();
        bean.setDocumentStatus(cheque.getDocumentStatus());
        bean.setDocumentType(cheque.getDocumentType());
        bean.setPostDate(cheque.getPostDate());
        Money regularPayemnt = Money.of(cheque.getAmount(), cheque.getLoan().getTerms().getLoanCurrency());
        bean.setAmount(regularPayemnt);
        bean.setReference(cheque.getReference());
        bean.setNote(cheque.getNote());
        bean.setBatchEntryEnabled(false);
        return bean;
    }



    public static List<Cheque> generateChequeBatch(ChequeEntryFormBean chequeEntryFormBean, Loan loan) {

        ArrayList<Cheque> result = new ArrayList<>();
        ReferenceIncrementer incrementingReference = new ReferenceIncrementer(chequeEntryFormBean.getReference());
        TimePeriod paymentFrequency = TimePeriod.getTimePeriodWithPeriodCountOf(loan.getTerms().getPaymentFrequency());

        for (int i = 0; i < chequeEntryFormBean.getBatch(); i++) {

            Cheque cheque = new Cheque();
            cheque.setLoan(loan);
            populateChequeWithFormValues(cheque, chequeEntryFormBean);

            // Overrides for extrapolating batches
            cheque.setPostDate(paymentFrequency.getDateFrom(cheque.getPostDate(), i));
            cheque.setReference(incrementingReference.getNext());

            result.add(cheque);
        }

        return result;

    }


    public static void populateChequeWithFormValues(Cheque cheque, ChequeEntryFormBean chequeEntryFormBean) {
        cheque.setDocumentType(chequeEntryFormBean.getDocumentType());
        cheque.setDocumentStatus(chequeEntryFormBean.getDocumentStatus());
        cheque.setPostDate(chequeEntryFormBean.getPostDate());
        cheque.setCurrency(chequeEntryFormBean.getAmount().getCurrency().getCurrencyCode());
        cheque.setAmount(chequeEntryFormBean.getAmount().getNumber().doubleValue());
        cheque.setReference(chequeEntryFormBean.getReference());
        cheque.setNote(chequeEntryFormBean.getNote());
    }

}
