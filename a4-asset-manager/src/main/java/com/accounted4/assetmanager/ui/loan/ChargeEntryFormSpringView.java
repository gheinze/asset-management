package com.accounted4.assetmanager.ui.loan;

import com.accounted4.assetmanager.repository.LoanRepository;
import com.accounted4.assetmanager.entity.LoanChargeType;
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
 * Popup form for the creation or editing of a charge against a loan.
 *
 * @author gheinze
 */
@UIScope
@SpringView()
public class ChargeEntryFormSpringView extends AbstractForm<ChargeEntryFormBean> implements DefaultView {


    private TypedSelect<LoanChargeType> chargeType;
    private final PopupDateField chargeDate = new AmPopupDateField("Charge date");
    private MTextField amount;
    private final MTextArea note = new MTextArea("Note");

    // Dependency on the loanRepo is for retrieving Combobox LOV options
    private final LoanRepository loanRepo;


    @Inject
    public ChargeEntryFormSpringView(LoanRepository loanRepo) {
        this.loanRepo = loanRepo;
    }


    @PostConstruct
    private void init() {
        prepareChargeTypeSelect();
        prepareAmountField();
    }


    private void prepareChargeTypeSelect() {
        chargeType = new TypedSelect<>(LoanChargeType.class);
        chargeType.setCaption("Charge type");
        chargeType.setBeans(loanRepo.getAllLoanChargeTypes());
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
                        chargeType
                        ,chargeDate
                        ,amount
                        ,note
                ).withWidth(""),
                getToolbar()
        ).withWidth("");
    }



    // ====================
    // == Exposed API
    // ====================

    public void setBackingBean(ChargeEntryFormBean chargeEntryFormBean) {
        setEntity(chargeEntryFormBean);
    }



}
