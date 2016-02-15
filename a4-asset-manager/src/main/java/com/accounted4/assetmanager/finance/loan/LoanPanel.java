package com.accounted4.assetmanager.finance.loan;

import com.accounted4.assetmanager.UiRouter;
import com.accounted4.assetmanager.util.vaadin.ui.SelectorDetailPanel;
import com.accounted4.finance.loan.AmortizationAttributes;
import com.vaadin.data.Property;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
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
public class LoanPanel extends SelectorDetailPanel<Loan> {

    private static final String LOAN_NAME_FIELD = "loanName";

    private final LoanRepository loanRepo;
    private final LoanService loanService;

    private final ChequeDisplay chequeDisplay;
    private final PaymentDisplay paymentDisplay;

    @Autowired
    public LoanPanel(
            LoanRepository loanRepo
            ,LoanService loanService
            ,ChequeDisplay chequeDisplay
            ,PaymentDisplay paymentDisplay
    ) {
        super("Loans");
        this.loanRepo = loanRepo;
        this.loanService = loanService;
        this.chequeDisplay = chequeDisplay;
        this.paymentDisplay = paymentDisplay;
        defineTabs();
    }



    private void defineTabs() {
        addDetailTab(getTermsPanelGenerator(), "Terms");
        addDetailTab(getPaymentDisplay(), "Payments");
        addDetailTab(getPsuedoButtonGenerator(), "Charges");
        addDetailTab(getChequeDisplay(), "Cheques");
    }

    // A function to generate the ui for the terms of the selected loan
    private Function<Loan, Component> getTermsPanelGenerator() {
        return (selectedLoan) -> {
            TermsPanel termsPanel = new TermsPanel(loanService, selectedLoan.getTerms().getAsAmAttributes());
            termsPanel.addFormChangeListner((event) -> {
                Property property = event.getProperty();
                AmortizationAttributes termsFromUi = (AmortizationAttributes)property.getValue();
                selectedLoan.getTerms().refreshFrom(termsFromUi);
                termsChangedAction(selectedLoan);
            });
            return termsPanel;
        };
    }

    private void termsChangedAction(Loan selectedLoan) {
        loanRepo.save(selectedLoan);
        LoanTerms newTermsFromDb = loanRepo.findOne(selectedLoan.getId()).getTerms();
        selectedLoan.setTerms(newTermsFromDb);
        // new Notification("Detected change to the terms", "", Notification.Type.TRAY_NOTIFICATION, true).show(Page.getCurrent());
    }


    // A function to generate the ui for the terms of the selected loan
    private Function<Loan, Component> getPaymentDisplay() {
        return (selectedLoan) -> {
            paymentDisplay.setLoan(selectedLoan);
            return paymentDisplay;
        };
    }

    private Function<Loan, Component> getPsuedoButtonGenerator() {
        return (selectedLoan) -> {
            return new Button(selectedLoan.toString());
        };
    }

    private Function<Loan, Component> getChequeDisplay() {
        return (selectedLoan) -> {
            chequeDisplay.setLoan(selectedLoan);
            return chequeDisplay;
        };
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
            terms.refreshFrom(TermsPanel.getDefaultAmortizationAttributes());
            terms.setLoan(newLoan);
            newLoan.setTerms(terms);

            loanRepo.save(newLoan);

        };

    }


}
