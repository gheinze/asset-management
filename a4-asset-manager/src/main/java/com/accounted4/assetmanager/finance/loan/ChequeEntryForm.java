package com.accounted4.assetmanager.finance.loan;

import com.accounted4.assetmanager.util.vaadin.ui.AmPopupDateField;
import com.accounted4.assetmanager.util.vaadin.ui.DefaultView;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Component;
import com.vaadin.ui.PopupDateField;
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
 * Popup form for the creation or editing of a payment reference (cheque, money order, ...)
 *
 * @author gheinze
 */
@UIScope
@SpringView()
public class ChequeEntryForm extends AbstractForm<ChequeEntryFormBean> implements DefaultView {

    // The key to viritin's MFormLayout is that the if the names of the components on the
    // form match the bound entity, all the binding is done implicitly
    private TypedSelect<PaymentDocumentType> documentType;
    private final PopupDateField postDate = new AmPopupDateField("Post date");
    private MTextField amount;
    private TypedSelect<PaymentDocumentStatus> documentStatus;
    private final MTextField reference = new MTextField("Reference");
    private final MTextField batch = new MTextField("Batch");
    private final MTextArea note = new MTextArea("Note");

    // Dependency on the loanRepo is for retrieving Combobox LOV options
    private final LoanRepository loanRepo;


    @Inject
    public ChequeEntryForm(LoanRepository loanRepo) {
        this.loanRepo = loanRepo;
    }


    @PostConstruct
    private void init() {
        preparePaymentDocumentTypeSelect();
        preparePaymentDocumentStatusSelect();
        prepareAmountField();
    }


    private void preparePaymentDocumentTypeSelect() {
        documentType = new TypedSelect<>(PaymentDocumentType.class);
        documentType.setCaption("Type");
        documentType.setNullSelectionAllowed(false);
        documentType.setBeans(loanRepo.getAllPaymentDocumentTypes());
    }



    private void preparePaymentDocumentStatusSelect() {
        documentStatus = new TypedSelect<>(PaymentDocumentStatus.class);
        documentStatus.setCaption("Status");
        documentStatus.setNullSelectionAllowed(false);
        documentStatus.setBeans(loanRepo.getAllPaymentDocumentStatus());
    }


    private void prepareAmountField() {
        amount = new MTextField("Amount");
        amount.setImmediate(true);
        amount.setConverter(MonetaryAmount.class);
    }




    @Override
    protected Component createContent() {
        return new MVerticalLayout(
                new MFormLayout(
                        documentType
                        ,postDate
                        ,amount
                        ,documentStatus
                        ,reference
                        ,batch
                        ,note
                ).withWidth(""),
                getToolbar()
        ).withWidth("");
    }



    // ====================
    // == Exposed API
    // ====================

    public void setBackingBean(ChequeEntryFormBean chequeEntryFormBean) {
        setEntity(chequeEntryFormBean);
        batch.setEnabled(chequeEntryFormBean.isBatchEntryEnabled());
    }


}
