package com.accounted4.assetmanager.ui.loan;

import com.accounted4.assetmanager.entity.Cheque;
import com.accounted4.assetmanager.util.vaadin.ui.AmPopupDateField;
import com.accounted4.assetmanager.util.vaadin.ui.DefaultView;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Component;
import javax.annotation.PostConstruct;
import javax.money.MonetaryAmount;
import org.javamoney.moneta.Money;
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
public class PaymentEntryFormSpringView extends AbstractForm<PaymentEntryFormBean> implements DefaultView {

    // The key to viritin's MFormLayout is that the if the names of the components on the
    // form match the bound entity, all the binding is done implicitly
    private TypedSelect<Cheque> cheque;
    private final AmPopupDateField depositDate = new AmPopupDateField("Deposit date");
    private MTextField amount;
    private final MTextArea note = new MTextArea("Note");


    @PostConstruct
    private void init() {
        prepareChequeSelect();
        prepareAmountField();
    }


    private void prepareChequeSelect() {
        cheque = new TypedSelect<>(Cheque.class);
        cheque.setCaption("Cheque");
        cheque.addMValueChangeListener(e -> {
            chequeSelectionChange(e.getValue());
        });
    }

    private void chequeSelectionChange(Cheque selectedCheque) {
        boolean enableEntry = null == selectedCheque;
        if (!enableEntry) {
            getEntity().setAmount(Money.of(selectedCheque.getAmount(), selectedCheque.getCurrency()));
        }
        amount.setEnabled(enableEntry);
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
                        cheque
                        ,depositDate
                        ,amount
                        ,note
                ).withWidth(""),
                getToolbar()
        ).withWidth("");
    }



    // ====================
    // == Exposed API
    // ====================

    public void setBackingBean(PaymentEntryFormBean paymentEntryFormBean) {
        setEntity(paymentEntryFormBean);
        cheque.setBeans(paymentEntryFormBean.getCheques());
    }



}
