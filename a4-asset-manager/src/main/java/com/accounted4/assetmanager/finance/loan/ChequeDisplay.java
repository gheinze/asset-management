package com.accounted4.assetmanager.finance.loan;

import com.accounted4.assetmanager.util.vaadin.ui.DefaultView;
import com.accounted4.assetmanager.util.vaadin.ui.FormEditToolBar;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Button;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import org.vaadin.viritin.fields.MTable;
import org.vaadin.viritin.layouts.MVerticalLayout;

/**
 * Display a list of cheques:
 *   o toolbar for modifying table entries
 *   o a table of cheques associated with a loan
 *   o a popup form for adding/modifying a cheque
 *
 * @author gheinze
 */
@UIScope
@SpringView
public class ChequeDisplay extends MVerticalLayout implements DefaultView {

    private static final long serialVersionUID = 1L;

    private final ChequeEntryForm chequeEntryForm;
    private final LoanRepository loanRepo;

    private final MTable<Cheque> chequeTable = new MTable<>(Cheque.class)
            .withProperties("documentType", "reference", "postDate", "amount", "documentStatus", "note")
            .withColumnHeaders("Type", "Reference", "Date", "Amount", "Status", "Note")
            .setSortableProperties("documentType")
            .withFullWidth();

    private Loan selectedLoan;

    private final FormEditToolBar editToolBar;


    @Inject
    public ChequeDisplay(ChequeEntryForm chequeEntryForm, LoanRepository loanRepo) {
        this.chequeEntryForm = chequeEntryForm;
        this.loanRepo = loanRepo;
        this.editToolBar = new FormEditToolBar(this::add, this::edit, this::remove);
    }

    @PostConstruct
    public void init() {
        addComponent(new MVerticalLayout(editToolBar, chequeTable).expand(chequeTable));
        chequeTable.addMValueChangeListener(e -> adjustActionButtonState());

    }

    protected void adjustActionButtonState() {
        boolean hasSelection = chequeTable.getValue() != null;
        editToolBar.adjustActionButtonState(hasSelection);
    }


    private void listCheques() {
        chequeTable.setBeans(selectedLoan.getCheques());
        adjustActionButtonState();
    }

    public void add(Button.ClickEvent clickEvent) {
        edit((Cheque)null);
    }

    public void edit(Button.ClickEvent e) {
        edit(chequeTable.getValue());
    }

    public void remove(Button.ClickEvent e) {
        selectedLoan.getCheques().remove(chequeTable.getValue());
        persistLoan();
    }

    protected void edit(final Cheque cheque) {
        chequeEntryForm.setCheque(cheque);
        chequeEntryForm.setSavedHandler(this::saveEntry);
        chequeEntryForm.setResetHandler(this::resetEntry);
        chequeEntryForm.openInModalPopup();
    }

    public void saveEntry(final Cheque cheque) {
        selectedLoan.getCheques().add(cheque);
        persistLoan();
        closeWindow();
    }

    private void persistLoan() {
        loanRepo.save(selectedLoan);
        selectedLoan = loanRepo.findOne(selectedLoan.getId());
        listCheques();
    }

    public void resetEntry(final Cheque cheque) {
        listCheques();
        closeWindow();
    }

    protected void closeWindow() {
        getUI().getWindows().stream().forEach(w -> getUI().removeWindow(w));
    }

    public void setLoan(Loan selectedLoan) {
        this.selectedLoan = selectedLoan;
        chequeEntryForm.setSelectedLoan(selectedLoan);
        listCheques();
    }

}
