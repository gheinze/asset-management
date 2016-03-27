package com.accounted4.assetmanager.ui.loan;

import com.accounted4.assetmanager.repository.LoanRepository;
import com.accounted4.assetmanager.entity.Loan;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import javax.inject.Inject;
import lombok.Getter;

/**
 * A controller for registration and notification of a change in the selected loan.
 *
 * @author gheinze
 */
@UIScope
@SpringComponent
public class SelectedLoanWrapper {

    @Getter
    private Loan selectedLoan;

    private final LoanRepository loanRepo;


    @Inject
    public SelectedLoanWrapper(LoanRepository loanRepo) {
        this.loanRepo = loanRepo;
    }


    private final List<Consumer<Loan>> changeListeners = new ArrayList<>();


    public void setSelectedLoan(Loan loan) {
        selectedLoan = loan;
        fireLoanUpdateListeners();
    }

    public void persist() {
        loanRepo.save(selectedLoan);
        setSelectedLoan(loanRepo.findOne(selectedLoan.getId()));
    }


    public void addLoanUpdateListener(Consumer<Loan> callBack) {
        changeListeners.add(callBack);
    }

    public void removeLoanUpdateListener(Consumer<Loan> callBack) {
        changeListeners.remove(callBack);
    }


    private void fireLoanUpdateListeners() {
        changeListeners.stream().forEach(listener -> listener.accept(selectedLoan));
    }

}
