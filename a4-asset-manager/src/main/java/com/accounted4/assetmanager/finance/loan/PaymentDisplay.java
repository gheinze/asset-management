package com.accounted4.assetmanager.finance.loan;

import com.accounted4.assetmanager.util.vaadin.ui.DefaultView;
import com.accounted4.assetmanager.util.vaadin.ui.FormEditToolBar;
import com.accounted4.assetmanager.util.vaadin.ui.Refreshable;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Button;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import org.javamoney.moneta.Money;
import org.vaadin.viritin.fields.MTable;
import org.vaadin.viritin.layouts.MVerticalLayout;

/**
 * Display a list of payments against a loan:
 *   o a toolbar for modifying table entries
 *   o a table of payments associated with a loan
 *   o a popup form for adding/modifying a payment
 *
 * @author gheinze
 */
@UIScope
@SpringView
public class PaymentDisplay extends MVerticalLayout implements DefaultView, Refreshable {

    private static final long serialVersionUID = 1L;

    private static final String[] DEFAULT_TABLE_SORT_PROPERTIES = {"depositDate"};
    private static final boolean[] DEFAULT_TABLE_SORT_DIRECTIONS = {true};


    private final PaymentEntryForm paymentEntryForm;
    private final LoanRepository loanRepo;

    private final MTable<LoanPayment> paymentTable = new MTable<>(LoanPayment.class)
            .withProperties("depositDate", "displayAmount", "cheque", "note")
            .withColumnHeaders("Deposit Date", "Amount", "Cheque", "Note")
            .setSortableProperties("depositDate")
            .withFullWidth();

    private Loan selectedLoan;

    private final FormEditToolBar editToolBar;


    @Inject
    public PaymentDisplay(PaymentEntryForm paymentEntryForm, LoanRepository loanRepo) {
        this.paymentEntryForm = paymentEntryForm;
        this.loanRepo = loanRepo;
        this.editToolBar = new FormEditToolBar(this::addNewPayment, this::editSelectedPayment, this::removeSelectedPayment);
    }

    @PostConstruct
    public void init() {
        paymentEntryForm.setSavedHandler(this::saveClickedOnEntryForm);
        paymentEntryForm.setResetHandler(this::cancelClickedOnEntryForm);
        addComponent(new MVerticalLayout(editToolBar, paymentTable).expand(paymentTable));
        paymentTable.addMValueChangeListener(e -> adjustActionButtonState());

    }

    private void adjustActionButtonState() {
        boolean aRowIsSelected = paymentTable.getValue() != null;
        editToolBar.adjustActionButtonState(aRowIsSelected);
    }


    private void refreshTable() {
        paymentTable.setBeans(selectedLoan.getPayments());
        paymentTable.sort(DEFAULT_TABLE_SORT_PROPERTIES, DEFAULT_TABLE_SORT_DIRECTIONS);
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

    private void addNewPayment(Button.ClickEvent clickEvent) {
        displayEntryFormPopup(getDefaultPaymentEntryFormBean());
    }

    private void editSelectedPayment(Button.ClickEvent e) {
        displayEntryFormPopup(convertPaymentEntityToPaymentEntryFormBean(paymentTable.getValue()));
    }

    private void removeSelectedPayment(Button.ClickEvent e) {
        selectedLoan.getPayments().remove(paymentTable.getValue());
        persistLoan();
    }


    private void displayEntryFormPopup(final PaymentEntryFormBean backingBean) {
        paymentEntryForm.setBackingBean(backingBean);
        paymentEntryForm.openInModalPopup();
    }


    // ====================
    // == Entry Form button click handlers
    // ====================

    private void saveClickedOnEntryForm(final PaymentEntryFormBean paymentFormBean) {
        selectedLoan.getPayments().add(convertPaymentEntryFormBeanToPayment(paymentFormBean));
        persistLoan();
        closeWindow();
    }

    private void cancelClickedOnEntryForm(final PaymentEntryFormBean paymentFormBean) {
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

    private PaymentEntryFormBean getDefaultPaymentEntryFormBean() {
        PaymentEntryFormBean bean = new PaymentEntryFormBean();
        Money regularPayemnt = Money.of(selectedLoan.getTerms().getRegularPayment(), selectedLoan.getTerms().getLoanCurrency());
        bean.setAmount(regularPayemnt);
        bean.setEditMode(false);
        bean.setDepositDate(LocalDate.now());
        bean.setCheques(getAvailableCheques());
        return bean;
    }


    private PaymentEntryFormBean convertPaymentEntityToPaymentEntryFormBean(LoanPayment payment) {

        PaymentEntryFormBean bean = new PaymentEntryFormBean();
        bean.setCheque(payment.getCheque());
        Money amount = Money.of(payment.getAmount(), payment.getCurrency());
        bean.setAmount(amount);
        bean.setDepositDate(payment.getDepositDate());
        bean.setNote(payment.getNote());

        bean.setEditMode(true);
        bean.setCheques(getAvailableCheques());

        return bean;
    }


    private LoanPayment convertPaymentEntryFormBeanToPayment(PaymentEntryFormBean paymentFormBean) {

        LoanPayment payment;

        if (paymentFormBean.isEditMode()) {
            payment = paymentTable.getValue();
        } else {
            payment = new LoanPayment();
            payment.setLoan(selectedLoan);
        }

        payment.setCheque(paymentFormBean.getCheque());
        payment.setAmount(paymentFormBean.getAmount().getNumber().doubleValue());
        payment.setCurrency(paymentFormBean.getAmount().getCurrency().getCurrencyCode());
        payment.setDepositDate(paymentFormBean.getDepositDate());
        payment.setNote(paymentFormBean.getNote());

        return payment;

    }


    private List<Cheque> getAvailableCheques() {
        return selectedLoan.getCheques().stream()
                .filter(cheque -> cheque.getDocumentStatus().getDocumentStatus().equals("On File"))
                .sorted( (c1, c2) -> c1.getPostDate().compareTo(c2.getPostDate()))
                .collect(Collectors.toList())
                ;
    }


    @Override
    public void refresh() {
        selectedLoan = loanRepo.findOne(selectedLoan.getId());
        refreshTable();
    }

}
