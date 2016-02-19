package com.accounted4.assetmanager.finance.loan;

import com.accounted4.assetmanager.util.vaadin.ui.AmMTable;
import com.accounted4.assetmanager.util.vaadin.ui.DefaultView;
import com.accounted4.assetmanager.util.vaadin.ui.Refreshable;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Table;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import org.vaadin.viritin.fields.MTable;
import org.vaadin.viritin.layouts.MVerticalLayout;

/**
 *
 * @author gheinze
 */
@UIScope
@SpringView
public class StatusDisplay extends MVerticalLayout implements DefaultView, Refreshable {

    private final MTable<LoanStatusLineItem> transactionTable = new AmMTable<>(LoanStatusLineItem.class)
            .withProperties("date",
                    "scheduledAmount", "scheduledInterest", "scheduledPrincipal", "scheduledBalance",
                    "transaction", "balance", "fees",
                    "type", "note")
            .withColumnHeaders("date",
                    "date", "scheduledInterest", "scheduledPrincipal", "scheduledBalance",
                    "transaction", "balance", "fees",
                    "type", "note")
            .setSortableProperties("date")
            .withFullWidth()
            ;

    private final LoanRepository loanRepo;
    private Loan selectedLoan;

    @Inject
    public StatusDisplay(LoanRepository loanRepo) {
        this.loanRepo = loanRepo;
    }


    @PostConstruct
    public void init() {

        transactionTable.setColumnAlignments(Table.Align.LEFT,
                Table.Align.RIGHT, Table.Align.RIGHT, Table.Align.RIGHT, Table.Align.RIGHT,
                Table.Align.RIGHT, Table.Align.RIGHT, Table.Align.RIGHT,
                Table.Align.LEFT, Table.Align.LEFT
        );
        transactionTable.setColumnCollapsingAllowed(true);
        transactionTable.setColumnCollapsed("scheduledAmount", true);
        transactionTable.setColumnCollapsed("scheduledInterest", true);
        transactionTable.setColumnCollapsed("scheduledPrincipal", true);
        transactionTable.setColumnCollapsed("scheduledBalance", true);

        addComponent(new MVerticalLayout(transactionTable).expand(transactionTable));
        withFullWidth();
        withFullHeight();
    }

    private void refreshTable() {
        LoanStatus status = new LoanStatus(selectedLoan);
        transactionTable.setBeans(status.getOrderedLineItems());
        String[] sortColumns = {"date"};
        boolean[] sortDirections = {true};
        transactionTable.sort(sortColumns, sortDirections);
    }


    // ====================
    // == Exposed API
    // ====================

    public void setLoan(Loan selectedLoan) {
        this.selectedLoan = selectedLoan;
        refreshTable();
    }

    @Override
    public void refresh() {
        selectedLoan = loanRepo.findOne(selectedLoan.getId());
        refreshTable();
    }

}
