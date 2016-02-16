package com.accounted4.assetmanager.finance.loan;

import com.accounted4.assetmanager.util.vaadin.ui.DefaultView;
import com.accounted4.assetmanager.util.vaadin.ui.FormEditToolBar;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Button;
import java.time.LocalDate;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import org.javamoney.moneta.Money;
import org.vaadin.viritin.fields.MTable;
import org.vaadin.viritin.layouts.MVerticalLayout;

/**
 * Display a list of charges against a loan:
 *   o a toolbar for modifying table entries
 *   o a table of charges associated with a loan
 *   o a popup form for adding/modifying a charge
 *
 * @author gheinze
 */
@UIScope
@SpringView
public class ChargeDisplay extends MVerticalLayout implements DefaultView {

    private static final long serialVersionUID = 1L;

    private static final String[] DEFAULT_TABLE_SORT_PROPERTIES = {"chargeDate"};
    private static final boolean[] DEFAULT_TABLE_SORT_DIRECTIONS = {true};


    private final ChargeEntryForm chargeEntryForm;
    private final LoanRepository loanRepo;

    private final MTable<LoanCharge> chargeTable = new MTable<>(LoanCharge.class)
            .withProperties("chargeDate", "displayAmount", "loanChargeType", "note")
            .withColumnHeaders("Charge Date", "Amount", "Charge Type", "Note")
            .setSortableProperties("chargeDate")
            .withFullWidth();

    private Loan selectedLoan;

    private final FormEditToolBar editToolBar;


    @Inject
    public ChargeDisplay(ChargeEntryForm chargeEntryForm, LoanRepository loanRepo) {
        this.chargeEntryForm = chargeEntryForm;
        this.loanRepo = loanRepo;
        this.editToolBar = new FormEditToolBar(this::addNewCharge, this::editSelectedCharge, this::removeSelectedCharge);
    }

    @PostConstruct
    public void init() {
        chargeEntryForm.setSavedHandler(this::saveClickedOnEntryForm);
        chargeEntryForm.setResetHandler(this::cancelClickedOnEntryForm);
        addComponent(new MVerticalLayout(editToolBar, chargeTable).expand(chargeTable));
        chargeTable.addMValueChangeListener(e -> adjustActionButtonState());

    }

    private void adjustActionButtonState() {
        boolean aRowIsSelected = chargeTable.getValue() != null;
        editToolBar.adjustActionButtonState(aRowIsSelected);
    }


    private void refreshTable() {
        chargeTable.setBeans(selectedLoan.getCharges());
        chargeTable.sort(DEFAULT_TABLE_SORT_PROPERTIES, DEFAULT_TABLE_SORT_DIRECTIONS);
        adjustActionButtonState();
    }


    // ====================
    // == Exposed API
    // ====================

    public void setLoan(Loan selectedLoan) {
        this.selectedLoan = selectedLoan;
        refreshTable();
    }


    // ====================
    // == Toolbar button click handlers
    // ====================

    private void addNewCharge(Button.ClickEvent clickEvent) {
        displayEntryFormPopup(getDefaultChargeEntryFormBean());
    }

    private void editSelectedCharge(Button.ClickEvent e) {
        displayEntryFormPopup(convertChargeEntityToChargeEntryFormBean(chargeTable.getValue()));
    }

    private void removeSelectedCharge(Button.ClickEvent e) {
        selectedLoan.getCharges().remove(chargeTable.getValue());
        persistLoan();
    }


    private void displayEntryFormPopup(final ChargeEntryFormBean backingBean) {
        chargeEntryForm.setBackingBean(backingBean);
        chargeEntryForm.openInModalPopup();
    }


    // ====================
    // == Entry Form button click handlers
    // ====================

    private void saveClickedOnEntryForm(final ChargeEntryFormBean chargeFormBean) {
        selectedLoan.getCharges().add(convertChargeEntryFormBeanToCharge(chargeFormBean));
        persistLoan();
        closeWindow();
    }

    private void cancelClickedOnEntryForm(final ChargeEntryFormBean chargeFormBean) {
        refreshTable();
        closeWindow();
    }

    private void persistLoan() {
        loanRepo.save(selectedLoan);
        selectedLoan = loanRepo.findOne(selectedLoan.getId());
        refreshTable();
    }

    private void closeWindow() {
        getUI().getWindows().stream().forEach(w -> getUI().removeWindow(w));
    }


    // ====================
    // == Helper utility
    // ====================

    private ChargeEntryFormBean getDefaultChargeEntryFormBean() {
        ChargeEntryFormBean bean = new ChargeEntryFormBean();

        // TODO: calculate: outstanding interest?
        Money regularPayemnt = Money.of(selectedLoan.getTerms().getRegularPayment(), selectedLoan.getTerms().getLoanCurrency());
        bean.setAmount(regularPayemnt);

        bean.setEditMode(false);
        bean.setChargeDate(LocalDate.now());  // TODO: calculate??
        bean.setChargeType(loanRepo.getDefaultChargeType());

        return bean;
    }


    private ChargeEntryFormBean convertChargeEntityToChargeEntryFormBean(LoanCharge charge) {

        ChargeEntryFormBean bean = new ChargeEntryFormBean();
        bean.setChargeType(charge.getLoanChargeType());
        Money amount = Money.of(charge.getAmount(), charge.getCurrency());
        bean.setAmount(amount);
        bean.setChargeDate(charge.getChargeDate());
        bean.setNote(charge.getNote());
        bean.setEditMode(true);

        return bean;
    }


    private LoanCharge convertChargeEntryFormBeanToCharge(ChargeEntryFormBean chargeFormBean) {

        LoanCharge charge;

        if (chargeFormBean.isEditMode()) {
            charge = chargeTable.getValue();
        } else {
            charge = new LoanCharge();
            charge.setLoan(selectedLoan);
        }

        charge.setLoanChargeType(chargeFormBean.getChargeType());
        charge.setAmount(chargeFormBean.getAmount().getNumber().doubleValue());
        charge.setCurrency(chargeFormBean.getAmount().getCurrency().getCurrencyCode());
        charge.setChargeDate(chargeFormBean.getChargeDate());
        charge.setNote(chargeFormBean.getNote());

        return charge;

    }


}
