package com.accounted4.assetmanager.ui.loan;

import com.accounted4.assetmanager.entity.Loan;
import com.accounted4.assetmanager.repository.LoanRepository;
import com.accounted4.assetmanager.util.Convert;
import com.accounted4.assetmanager.util.vaadin.ui.DefaultView;
import com.accounted4.assetmanager.util.vaadin.ui.Refreshable;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.PopupView;
import java.time.LocalDate;
import java.util.Date;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import org.vaadin.viritin.layouts.MHorizontalLayout;
import org.vaadin.viritin.layouts.MVerticalLayout;

/**
 *
 * @author gheinze
 */
@UIScope
@SpringView
public class AdminSpringView extends MVerticalLayout implements DefaultView, Refreshable {

    @Inject private LoanRepository loanRepo;
    private Loan selectedLoan;


    private final Label openClosedStateLabel = new Label();
    private final String mortgageOpenMsg = "This mortgage is <b><i>Open</i></b> and is still accruing interest.";
    private final String mortgageClosedMsg = "This mortgage has been <b><i>Closed</i></b> as of ";
    private final Button openClosedStateButton = new Button();
    private final PopupDateField closeDatePopup;
    private final PopupView loanCloseDatePopup;

    private final Label activeStateLabel = new Label();
    private final String mortgageInActiveMsg = "This mortgage is <b><i>Inactiva</i></b> and will not show up in list selections by default.";
    private final String mortgageActiveMsg = "This mortgage is <b><i>Active</i></b> and will show up in list selections by default.";
    private final Button activeStateButton = new Button();

    public AdminSpringView() {
        closeDatePopup = getCloseDatePopup();
        loanCloseDatePopup = new PopupView(null, closeDatePopup);
    }

    @PostConstruct
    private void init() {

        MHorizontalLayout row1 = configureOpenStateRow();
        MHorizontalLayout row2 = configureInactiveStateRow();

        addComponents(row1, row2);

        setSizeUndefined();

    }


    private MHorizontalLayout configureOpenStateRow() {

        loanCloseDatePopup.addPopupVisibilityListener(event -> {
            boolean popupIsClosing = !event.isPopupVisible();
            if (popupIsClosing) {
                LocalDate closeDate = null;
                boolean loanIsOpen = null == selectedLoan.getCloseDate();
                if (loanIsOpen) {
                    closeDate = Convert.dateToLocalDate(closeDatePopup.getValue());
                }
                selectedLoan.setCloseDate(closeDate);
                persistLoan();
            }
        });

        openClosedStateLabel.setWidthUndefined();
        openClosedStateLabel.setContentMode(ContentMode.HTML);

        openClosedStateButton.addClickListener(e -> {
            boolean loanIsOpen = null == selectedLoan.getCloseDate();
            if (loanIsOpen) {
                loanCloseDatePopup.setPopupVisible(true);
            } else {
                selectedLoan.setCloseDate(null);
                persistLoan();
            }
        });

        MHorizontalLayout row = new MHorizontalLayout(openClosedStateLabel, openClosedStateButton, loanCloseDatePopup);
        row.setComponentAlignment(openClosedStateLabel, Alignment.MIDDLE_LEFT);
        row.setComponentAlignment(openClosedStateButton, Alignment.MIDDLE_LEFT);
        row.setExpandRatio(openClosedStateButton, 1.0f);

        return row;
    }


    private MHorizontalLayout configureInactiveStateRow() {

        activeStateLabel.setWidthUndefined();
        activeStateLabel.setContentMode(ContentMode.HTML);

        activeStateButton.addClickListener(e -> {
            selectedLoan.setInactive(!selectedLoan.getInactive());
            persistLoan();
        });

        MHorizontalLayout row = new MHorizontalLayout(activeStateLabel, activeStateButton);
        row.setComponentAlignment(activeStateLabel, Alignment.MIDDLE_LEFT);
        row.setComponentAlignment(activeStateButton, Alignment.MIDDLE_LEFT);

        return row;
    }


    private void persistLoan() {
        loanRepo.save(selectedLoan);
        selectedLoan = loanRepo.findOne(selectedLoan.getId());
        refresh();
    }

    @Override
    public void refresh() {
        refreshOpenState();
        refreshInactiveState();
    }


    private void refreshOpenState() {
        LocalDate closeDate = selectedLoan.getCloseDate();
        boolean closed = (null != closeDate);
        openClosedStateLabel.setValue(closed ? mortgageClosedMsg + closeDate.toString() : mortgageOpenMsg);
        openClosedStateButton.setCaption(closed ? "Re-open" : "Close");
    }

    private void refreshInactiveState() {
        boolean inactive = selectedLoan.getInactive();
        activeStateLabel.setValue(inactive ? mortgageInActiveMsg : mortgageActiveMsg);
        activeStateButton.setCaption(inactive ? "Mark as Active" : "Mark as Inactive");
    }


    public void setLoan(Loan selectedLoan) {
        this.selectedLoan = selectedLoan;
        refresh();
    }



    private PopupDateField getCloseDatePopup() {

        PopupDateField calendarPopup;

        calendarPopup = new PopupDateField("Closing date");
        calendarPopup.setConverter(LocalDate.class);
        calendarPopup.setDateFormat("dd-MMM-yyyy");
        calendarPopup.setWidth("10em");
        calendarPopup.setImmediate(true);
        calendarPopup.setValue(new Date());

        return calendarPopup;

    }


}
