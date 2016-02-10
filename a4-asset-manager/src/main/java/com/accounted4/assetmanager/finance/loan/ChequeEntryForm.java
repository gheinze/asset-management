package com.accounted4.assetmanager.finance.loan;

import com.accounted4.assetmanager.util.vaadin.ui.DefaultView;
import com.accounted4.finance.loan.AmortizationCalculator;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Component;
import com.vaadin.ui.PopupDateField;
import java.time.LocalDate;
import java.util.Optional;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.money.MonetaryAmount;
import org.vaadin.viritin.fields.MTextArea;
import org.vaadin.viritin.fields.MTextField;
import org.vaadin.viritin.fields.TypedSelect;
import org.vaadin.viritin.form.AbstractForm;
import org.vaadin.viritin.layouts.MFormLayout;
import org.vaadin.viritin.layouts.MVerticalLayout;

/**
 * Popup form for the creation of a payment reference (cheque, money order, ...)
 *
 * @author gheinze
 */
@UIScope
@SpringView()
public class ChequeEntryForm extends AbstractForm<Cheque> implements DefaultView {

    private PaymentDocumentType defaultDocumentType;
    private PaymentDocumentStatus defaultDocumentStatus;
    private Loan selectedLoan;


    private TypedSelect<PaymentDocumentType> documentType;
    private PopupDateField postDate;
    private MTextField displayAmount;
    private TypedSelect<PaymentDocumentStatus> documentStatus;
    private final MTextField reference = new MTextField("Reference");
    private final MTextArea note = new MTextArea("Note");

    private final LoanRepository loanRepo;


    @Inject
    public ChequeEntryForm(LoanRepository loanRepo) {
        this.loanRepo = loanRepo;
    }


    @PostConstruct
    private void init() {
        preparePaymentDocumentTypeSelect();
        preparePaymentDocumentStatusSelect();
        prepareDateField();
        prepareAmountField();
    }


    private void preparePaymentDocumentTypeSelect() {
        documentType = new TypedSelect<>(PaymentDocumentType.class);
        documentType.setCaption("Type");
        documentType.setBeans(loanRepo.getAllPaymentDocumentTypes());
        configureDefaultDocumentType();
    }

    private void configureDefaultDocumentType() {
        Optional<PaymentDocumentType> foundType = documentType.getOptions()
                .stream()
                .filter(docType -> docType.getDocumentType().equals("Cheque"))
                .limit(1)
                .findFirst()
                ;
        defaultDocumentType = foundType.isPresent() ? foundType.get() : null;
    }


    private void preparePaymentDocumentStatusSelect() {
        documentStatus = new TypedSelect<>(PaymentDocumentStatus.class);
        documentStatus.setCaption("Status");
        documentStatus.setBeans(loanRepo.getAllPaymentDocumentStatus());
        configureDefaultDocumentStatus();
    }

    private void configureDefaultDocumentStatus() {
        Optional<PaymentDocumentStatus> foundType = documentStatus.getOptions()
                .stream()
                .filter(docType -> docType.getDocumentStatus().equals("On File"))
                .limit(1)
                .findFirst()
                ;
        defaultDocumentStatus = foundType.isPresent() ? foundType.get() : null;
    }



    private void prepareDateField() {
        postDate = new PopupDateField("Post date");
        postDate.setConverter(LocalDate.class);
        postDate.setImmediate(true);
    }


    private void prepareAmountField() {
        displayAmount = new MTextField("Amount");
        displayAmount.setImmediate(true);
        displayAmount.setConverter(MonetaryAmount.class);
    }




    public void setCheque(Cheque cheque) {
        setEntity(null == cheque ? createNewCheque() : cheque);
    }


    private Cheque createNewCheque() {

        Cheque cheque = new Cheque();
        cheque.setLoan(selectedLoan);
        cheque.setCurrency(selectedLoan.getTerms().getLoanCurrency());
        cheque.setDocumentType(defaultDocumentType);
        cheque.setDocumentStatus(defaultDocumentStatus);

        if (null != selectedLoan) {
            cheque.setPostDate(selectedLoan.getTerms().getAdjustmentDate());
            cheque.setAmount(selectedLoan.getTerms().getRegularPayment());
        } else {
            LocalDate nextFirstOrFifteenthOfTheMonth = AmortizationCalculator.getNextFirstOrFifteenthOfTheMonth(LocalDate.now());
            cheque.setPostDate(nextFirstOrFifteenthOfTheMonth);
        }

        return cheque;
    }


    @Override
    protected Component createContent() {
        return new MVerticalLayout(
                new MFormLayout(
                        documentType
                        ,postDate
                        ,displayAmount
                        ,documentStatus
                        ,reference
                        ,note
                ).withWidth(""),
                getToolbar()
        ).withWidth("");
    }


    void setSelectedLoan(Loan selectedLoan) {
        this.selectedLoan = selectedLoan;
    }


}
