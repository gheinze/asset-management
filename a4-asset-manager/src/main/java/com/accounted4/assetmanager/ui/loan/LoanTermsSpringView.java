package com.accounted4.assetmanager.ui.loan;

import com.accounted4.assetmanager.service.LoanService;
import com.accounted4.assetmanager.repository.LoanRepository;
import com.accounted4.assetmanager.entity.Loan;
import com.accounted4.assetmanager.util.vaadin.ui.DefaultView;
import com.accounted4.assetmanager.util.vaadin.ui.Refreshable;
import com.accounted4.finance.loan.AmortizationAttributes;
import com.vaadin.data.Property;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.UIScope;
import javax.inject.Inject;
import org.vaadin.viritin.layouts.MVerticalLayout;

/**
 * Display terms for a selected loan
 *
 * @author gheinze
 */
@UIScope
@SpringView
public class LoanTermsSpringView extends MVerticalLayout implements DefaultView, Refreshable {

    private static final long serialVersionUID = 1L;

    private final LoanRepository loanRepo;
    private final LoanService loanService;

    private LoanTermsPanel termsPanel;
    private Loan selectedLoan;


    @Inject
    public LoanTermsSpringView(LoanRepository loanRepo, LoanService loanService) {
        this.loanRepo = loanRepo;
        this.loanService = loanService;
    }

    private void createTermsPanel(AmortizationAttributes amAttrs) {
        termsPanel = new LoanTermsPanel(loanService, amAttrs);
        termsPanel.addFormChangeListner((event) -> {
            Property property = event.getProperty();
            AmortizationAttributes termsFromUi = (AmortizationAttributes) property.getValue();
            selectedLoan.getTerms().refreshFrom(termsFromUi);
            termsChangedAction();
        });
        removeAllComponents();
        addComponent(new MVerticalLayout(termsPanel).expand(termsPanel));
        withFullWidth();
        withFullHeight();
    }

    private void termsChangedAction() {
        loanRepo.save(selectedLoan);
        selectedLoan = loanRepo.findOne(selectedLoan.getId());
        // new Notification("Detected change to the terms", "", Notification.Type.TRAY_NOTIFICATION, true).show(Page.getCurrent());
    }


    // ====================
    // == Exposed API
    // ====================

    public void setLoan(Loan selectedLoan) {
        this.selectedLoan = selectedLoan;
        refresh();
    }


    @Override
    public void refresh() {
        selectedLoan = loanRepo.findOne(selectedLoan.getId());
        createTermsPanel(selectedLoan.getTerms().getAsAmAttributes());
    }

}
