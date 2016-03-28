package com.accounted4.assetmanager.ui.loan;

import com.accounted4.assetmanager.repository.LoanRepository;
import com.accounted4.assetmanager.entity.LoanTerms;
import com.accounted4.assetmanager.entity.Loan;
import com.accounted4.assetmanager.UiRouter;
import com.accounted4.assetmanager.entity.LoanNote;
import com.accounted4.assetmanager.repository.LoanNoteRepository;
import com.accounted4.assetmanager.util.vaadin.ui.SelectorDetailPanel;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Component;
import com.vaadin.ui.RichTextArea;
import java.util.function.Consumer;
import java.util.function.Function;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;

/**
 * Master-detail panel for displaying loans.
 *
 * @author gheinze
 */
@UIScope
@SpringView(name = UiRouter.ViewName.LOANS)
public class LoanSpringView extends SelectorDetailPanel<Loan> {

    private static final String LOAN_NAME_FIELD = "loanName";

    private final LoanRepository loanRepo;
    private final LoanNoteRepository loanNoteRepo;

    private final LoanTermsSpringView termsDisplay;
    private final ChequeSpringView chequeDisplay;
    private final PaymentSpringView paymentDisplay;
    private final ChargeSpringView chargeDisplay;
    private final LoanStatusSpringView statusDisplay;


    @Autowired
    public LoanSpringView(
            LoanRepository loanRepo
            ,LoanNoteRepository loanNoteRepo
            ,LoanTermsSpringView termsDisplay
            ,ChequeSpringView chequeDisplay
            ,PaymentSpringView paymentDisplay
            ,ChargeSpringView chargeDisplay
            ,LoanStatusSpringView statusDisplay
    ) {
        super("Loans");
        this.loanRepo = loanRepo;
        this.loanNoteRepo = loanNoteRepo;
        this.termsDisplay = termsDisplay;
        this.chequeDisplay = chequeDisplay;
        this.paymentDisplay = paymentDisplay;
        this.chargeDisplay = chargeDisplay;
        this.statusDisplay = statusDisplay;
        defineTabs();
    }



    private void defineTabs() {
        addDetailTab(getTermsDisplay(), "Terms");
        addDetailTab(getPaymentDisplay(), "Payments");
        addDetailTab(getChargeDisplay(), "Charges");
        addDetailTab(getChequeDisplay(), "Cheques");
        addDetailTab(getStatusDisplay(), "Status");
        addDetailTab(getNotesAreaGenerator(), "Notes");
    }

    // A function to generate the ui for the terms of the selected loan
    private Function<Loan, Component> getTermsDisplay() {
        return (selectedLoan) -> {
            termsDisplay.setLoan(selectedLoan);
            return termsDisplay;
        };
    }



    private Function<Loan, Component> getPaymentDisplay() {
        return (selectedLoan) -> {
            paymentDisplay.setLoan(selectedLoan);
            return paymentDisplay;
        };
    }


    private Function<Loan, Component> getChargeDisplay() {
        return (selectedLoan) -> {
            chargeDisplay.setLoan(selectedLoan);
            return chargeDisplay;
        };
    }


    private Function<Loan, Component> getChequeDisplay() {
        return (selectedLoan) -> {
            chequeDisplay.setLoan(selectedLoan);
            return chequeDisplay;
        };
    }


    private Function<Loan, Component> getStatusDisplay() {
        return (selectedLoan) -> {
            statusDisplay.setLoan(selectedLoan);
            return statusDisplay;
        };
    }


    private Function<Loan, Component> getNotesAreaGenerator() {

        return (selectedLoan) -> {

            RichTextArea noteArea = new RichTextArea();
            noteArea.addStyleName("noImageButton");
            noteArea.setWidth("100%");
            noteArea.setHeight("100%");

            String richText = getLoanNote(selectedLoan).getNote();
            noteArea.setValue(null == richText ? "" : richText);

            noteArea.addValueChangeListener(event -> {
                LoanNote loanNote = getLoanNote(selectedLoan);
                loanNote.setNote(noteArea.getValue());
                loanNoteRepo.save(loanNote);
            });

            return noteArea;
        };
    }

    private LoanNote getLoanNote(Loan selectedLoan) {
        LoanNote loanNote = selectedLoan.getNote();
        if (null == loanNote) {
            loanNote = new LoanNote();
            loanNote.setLoan(selectedLoan);
            selectedLoan.setNote(loanNote);
        }
        return loanNote;
    }


    // Function to build the backing data structure of the loan selector combobox.
    @Override
    public Function<Boolean, BeanContainer<String, Loan>> getBeanContainerGenerator() {

        return (showInactive) -> {
            BeanContainer<String, Loan> beanContainer = new BeanContainer<>(Loan.class);
            beanContainer.setBeanIdProperty(LOAN_NAME_FIELD);
            beanContainer.addAll(
                    showInactive
                            ? loanRepo.findAll(new Sort(LOAN_NAME_FIELD))
                            : loanRepo.findByInactiveOrderByLoanName(false)
            );
            return beanContainer;
        };

    }


    // Function to persist a new loan to the db
    @Override
    public Consumer<String> getNewItemPersistor() {

        return (loanName) -> {

            Loan newLoan = new Loan();
            newLoan.setLoanName(loanName);
            newLoan.setInactive(false);

            LoanTerms terms = new LoanTerms();
            terms.refreshFrom(LoanTermsPanel.getDefaultAmortizationAttributes());
            terms.setLoan(newLoan);
            newLoan.setTerms(terms);

            loanRepo.save(newLoan);

        };

    }


}
