package com.accounted4.assetmanager.ui.loan;

import com.accounted4.assetmanager.ui.loan.status.LoanStatus;
import com.accounted4.assetmanager.entity.Cheque;
import com.vaadin.ui.Panel;
import java.util.Optional;
import javax.money.MonetaryAmount;
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


    private final int NUMBER_OF_DAYS_BEFORE_MATURITY_TO_WARN = 45;


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
        regularDue.setValue(formatMoney(loanStatus.getRegularDue()));

        actualDue.setValue(formatMoney(loanStatus.getActualDue()));
        if (loanStatus.getActualDue().isGreaterThan(loanStatus.getRegularDue())) {
            actualDue.addStyleName("redLabel");
        } else {
            actualDue.removeStyleName("redLabel");
        }

        balance.setValue(formatMoney(loanStatus.getBalance()));

        Optional<Cheque> cheque = loanStatus.getNextChequeOnFile();
        nextChequeOnFile.setValue(cheque.isPresent() ? cheque.toString() : "No cheque on file");

        daysToMaturity.setValue(loanStatus.getDaysToMaturity().toString());
        if (loanStatus.getDaysToMaturity() <= NUMBER_OF_DAYS_BEFORE_MATURITY_TO_WARN) {
            daysToMaturity.addStyleName("redLabel");
        } else {
            daysToMaturity.removeStyleName("redLabel");
        }

        perDiem.setValue(formatMoney(loanStatus.getPerDiem()));
    }


    private String formatMoney(MonetaryAmount amount) {
        return String.format("$%(,.2f", amount.getNumber().doubleValue());
    }

}
