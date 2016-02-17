package com.accounted4.assetmanager.finance.loan;

import com.accounted4.assetmanager.util.vaadin.ui.DefaultView;
import com.accounted4.assetmanager.util.vaadin.ui.FormEditToolBar;
import com.accounted4.assetmanager.util.vaadin.ui.Refreshable;
import com.accounted4.finance.loan.TimePeriod;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Button;
import com.vaadin.ui.Table;
import java.time.LocalDate;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import org.javamoney.moneta.Money;
import org.vaadin.viritin.fields.MTable;
import org.vaadin.viritin.layouts.MVerticalLayout;

/**
 * Display a list of cheques:
 *   o a toolbar for modifying table entries
 *   o a table of cheques associated with a loan
 *   o a popup form for adding/modifying a cheque
 *
 * @author gheinze
 */
@UIScope
@SpringView
public class ChequeDisplay extends MVerticalLayout implements DefaultView, Refreshable {

    private static final long serialVersionUID = 1L;

    private static final String[] DEFAULT_TABLE_SORT_PROPERTIES = {"postDate"};
    private static final boolean[] DEFAULT_TABLE_SORT_DIRECTIONS = {true};


    private final ChequeEntryForm chequeEntryForm;
    private final LoanRepository loanRepo;

    private final MTable<Cheque> chequeTable = new MTable<>(Cheque.class)
            .withProperties("documentType", "reference", "postDate", "displayAmount", "documentStatus", "note")
            .withColumnHeaders("Type", "Reference", "Date", "Amount", "Status", "Note")
            .setSortableProperties("documentType", "reference", "postDate", "displayAmount", "documentStatus")
            .withFullWidth();

    private Loan selectedLoan;

    private final FormEditToolBar editToolBar;


    @Inject
    public ChequeDisplay(ChequeEntryForm chequeEntryForm, LoanRepository loanRepo) {
        this.chequeEntryForm = chequeEntryForm;
        this.loanRepo = loanRepo;
        this.editToolBar = new FormEditToolBar(this::addNewCheque, this::editSelectedCheque, this::removeSelectedCheque);
    }

    @PostConstruct
    public void init() {

        chequeEntryForm.setSavedHandler(this::saveClickedOnChequeEntryForm);
        chequeEntryForm.setResetHandler(this::cancelClickedOnChequeEntryForm);
        addComponent(new MVerticalLayout(editToolBar, chequeTable).expand(chequeTable));

        chequeTable.setColumnAlignments(Table.Align.LEFT, Table.Align.LEFT, Table.Align.LEFT, Table.Align.RIGHT, Table.Align.LEFT, Table.Align.LEFT);
        chequeTable.addMValueChangeListener(e -> adjustActionButtonState());

        withFullWidth();
        withFullHeight();
   }

    private void adjustActionButtonState() {
        boolean aChequeIsSelected = chequeTable.getValue() != null;
        editToolBar.adjustActionButtonState(aChequeIsSelected);
    }


    private void listCheques() {
        chequeTable.setBeans(selectedLoan.getCheques());
        chequeTable.sort(DEFAULT_TABLE_SORT_PROPERTIES, DEFAULT_TABLE_SORT_DIRECTIONS);
        adjustActionButtonState();
    }



    // ====================
    // == Exposed API
    // ====================

    public void setLoan(Loan selectedLoan) {
        this.selectedLoan = selectedLoan;
        listCheques();
    }


    // ====================
    // == Toolbar button click handlers
    // ====================

    private void addNewCheque(Button.ClickEvent clickEvent) {
        displayEntryFormPopup(getDefaultChequeEntryFormBean());
    }

    private void editSelectedCheque(Button.ClickEvent e) {
        displayEntryFormPopup(ChequeBeanConversionUtils.getChequeEntryFormBean(chequeTable.getValue()));
    }

    private void removeSelectedCheque(Button.ClickEvent e) {
        selectedLoan.removeCheque(chequeTable.getValue());
        persistLoan();
    }


    private void displayEntryFormPopup(final ChequeEntryFormBean chequeEntryFormBean) {
        chequeEntryForm.setBackingBean(chequeEntryFormBean);
        chequeEntryForm.openInModalPopup();
    }


    // ====================
    // == Entry Form button click handlers
    // ====================

    private void saveClickedOnChequeEntryForm(final ChequeEntryFormBean chequeEntryFormBean) {
        if (chequeEntryFormBean.isBatchEntryEnabled()) { //insert
            List<Cheque> cheques = ChequeBeanConversionUtils.generateChequeBatch(chequeEntryFormBean, selectedLoan);
            selectedLoan.getCheques().addAll(cheques);
        } else { // update
            ChequeBeanConversionUtils.populateChequeWithFormValues(chequeTable.getValue(), chequeEntryFormBean);
        }
        persistLoan();
        closeWindow();
    }

    private void cancelClickedOnChequeEntryForm(final ChequeEntryFormBean chequeEntryFormBean) {
        listCheques();
        closeWindow();
    }

    private void persistLoan() {
        loanRepo.save(selectedLoan);
        selectedLoan = loanRepo.findOne(selectedLoan.getId());
        listCheques();
    }

    private void closeWindow() {
        getUI().getWindows().stream().forEach(w -> getUI().removeWindow(w));
    }


    // ====================
    // == Helper utility
    // ====================

    private ChequeEntryFormBean getDefaultChequeEntryFormBean() {
        ChequeEntryFormBean bean = new ChequeEntryFormBean();
        bean.setDocumentStatus(loanRepo.getDefaultPaymentDocumentStatus());
        bean.setDocumentType(loanRepo.getDefaultPaymentDocumentType());

        // TODO: make this a function, base it off of the last cheque if it exists?
        TimePeriod paymentFrequency = TimePeriod.getTimePeriodWithPeriodCountOf(selectedLoan.getTerms().getPaymentFrequency());
        LocalDate firstPaymentDate = paymentFrequency.getDateFrom(selectedLoan.getTerms().getAdjustmentDate(), 1);
        bean.setPostDate(firstPaymentDate);

        Money regularPayemnt = Money.of(selectedLoan.getTerms().getRegularPayment(), selectedLoan.getTerms().getLoanCurrency());
        bean.setAmount(regularPayemnt);

        bean.setBatchEntryEnabled(true);

        return bean;
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * A cheque may have been consumed by a payment, so refresh if the tab is selected...
     */
    @Override
    public void refresh() {
        selectedLoan = loanRepo.findOne(selectedLoan.getId());
        listCheques();
    }


}
