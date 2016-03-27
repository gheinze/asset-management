package com.accounted4.assetmanager.finance.loan;

import com.accounted4.assetmanager.entity.LoanTerms;
import com.accounted4.assetmanager.entity.Loan;
import com.accounted4.assetmanager.UiRouter;
import com.accounted4.assetmanager.util.vaadin.ui.SelectorDetailPanel;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.UIScope;
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

    private final TermsDisplay termsDisplay;
    private final ChequeDisplay chequeDisplay;
    private final PaymentDisplay paymentDisplay;
    private final ChargeDisplay chargeDisplay;
    private final StatusDisplay statusDisplay;


    @Autowired
    public LoanPanel(
            LoanRepository loanRepo
            ,TermsDisplay termsDisplay
            ,ChequeDisplay chequeDisplay
            ,PaymentDisplay paymentDisplay
            ,ChargeDisplay chargeDisplay
            ,StatusDisplay statusDisplay
    ) {
        super("Loans");
        this.loanRepo = loanRepo;
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
