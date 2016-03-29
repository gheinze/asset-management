package com.accounted4.assetmanager.ui.loan;

import com.vaadin.ui.Panel;
import org.vaadin.viritin.label.MLabel;
import org.vaadin.viritin.layouts.MFormLayout;
import org.vaadin.viritin.layouts.MHorizontalLayout;

/**
 *
 * @author gheinze
 */
public class LoanStatusSummaryPanel extends Panel {

    private final MLabel nextPaymentDate;
    private final MLabel regularDue;
    private final MLabel actualDue;
    private final MLabel balance;
    private final MLabel nextChequeOnFile;
    private final MLabel daysToMaturity;
    private final MLabel perDiem;


    public LoanStatusSummaryPanel() {

        nextPaymentDate = new MLabel();
        nextPaymentDate.setCaption("As of");

        regularDue = new MLabel();
        regularDue.setCaption("Regular payment");

        actualDue = new MLabel();
        actualDue.setCaption("Acutal due");

        balance = new MLabel();
        balance.setCaption("Balance");

        MFormLayout col1 = new MFormLayout();
        col1.addComponents(nextPaymentDate, regularDue, actualDue, balance);


        nextChequeOnFile = new MLabel();
        nextChequeOnFile.setCaption("Next cheque on file");

        daysToMaturity = new MLabel();
        daysToMaturity.setCaption("Days to maturity");

        perDiem = new MLabel();
        perDiem.setCaption("Per diem");

        MFormLayout col2 = new MFormLayout();
        col2.addComponents(nextChequeOnFile, daysToMaturity, perDiem);

        MHorizontalLayout horizontalLayout = new MHorizontalLayout(col1, col2);
        setContent(horizontalLayout);

    }

    public void refresh(LoanStatus loanStatus) {

        nextPaymentDate.setValue(loanStatus.getNextScheduledPaymentDate().toString());
        regularDue.setValue(loanStatus.getRegularDue().toString());
        actualDue.setValue(loanStatus.getActualDue().toString());
        balance.setValue(loanStatus.getBalance().toString());

        nextChequeOnFile.setValue(loanStatus.getNextChequeOnFile());
        daysToMaturity.setValue(loanStatus.getDaysToMaturity().toString());
        perDiem.setValue(loanStatus.getPerDiem().toString());
    }


}