package com.accounted4.assetmanager.loan;

import com.accounted4.assetmanager.UiRouter;
import com.accounted4.assetmanager.util.vaadin.ui.SelectorDetailPanel;
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

    @Autowired
    public LoanPanel(LoanRepository loanRepo) {
        super("Loans");
        this.loanRepo = loanRepo;
        defineTabs();
    }



    private void defineTabs() {
        addDetailTab(getPsuedoButtonGenerator(), "Terms");
    }

    private Function<Loan, Component> getPsuedoButtonGenerator() {
        return (selectedLoan) -> {
            return new Button(selectedLoan.toString());
        };
    }

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


    @Override
    public Consumer<String> getNewItemPersistor() {

        return (loanName) -> {
            Loan newLoan = new Loan();
            newLoan.setLoanName(loanName);
            newLoan.setInactive(false);
            loanRepo.save(newLoan);
        };

    }


}
