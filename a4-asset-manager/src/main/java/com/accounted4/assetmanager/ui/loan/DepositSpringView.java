package com.accounted4.assetmanager.ui.loan;

import com.accounted4.assetmanager.ui.loan.status.LoanStatus;
import com.accounted4.assetmanager.UiRouter;
import com.accounted4.assetmanager.entity.Loan;
import com.accounted4.assetmanager.service.LoanService;
import com.accounted4.assetmanager.util.vaadin.ui.AmMTable;
import com.accounted4.assetmanager.util.vaadin.ui.DefaultView;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Panel;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Table;
import com.vaadin.ui.themes.Reindeer;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import org.vaadin.viritin.fields.MTable;
import org.vaadin.viritin.layouts.MFormLayout;
import org.vaadin.viritin.layouts.MVerticalLayout;

/**
 *
 * @author gheinze
 */
@UIScope
@SpringView(name = UiRouter.ViewName.DEPOSIT)
public class DepositSpringView extends Panel implements DefaultView {


    @Inject LoanService loanService;

    private PopupDateField depositDatePopup;


    private final String[] fieldNames = { "selected", "loanName", "regularPayment", "asOf", "currentDue", "cheque" };

    private final MTable<DepositLineItem> depositTable = new AmMTable<>(DepositLineItem.class)
            .withProperties(fieldNames)
            .withColumnHeaders(fieldNames)
            .setSortableProperties("dueDate")
            .withFullWidth()
            ;


    @PostConstruct
    public void init() {

        setCaption("Cheque Deposit");

        depositDatePopup = createDateField("Deposit Date");
        depositDatePopup.setValue(new Date());
        Button depositButton = createDepositButton();

        MFormLayout depositForm = new MFormLayout(depositDatePopup).withWidth("");
        configureDepositTable();

        MVerticalLayout layout = new MVerticalLayout();
        layout.addComponents(depositForm, depositButton, depositTable);
        layout.setSizeFull();
        layout.expand(depositTable);

        setContent(layout);
        setSizeFull();
        addStyleName(Reindeer.PANEL_LIGHT);

        refresh();
    }


    private PopupDateField createDateField(String caption) {
        PopupDateField field = new PopupDateField(caption);
        field.setConverter(LocalDate.class);
        field.setDateFormat("dd-MMM-yyyy");
        field.setWidth("10em");
        field.setImmediate(true);
        return field;
    }


    private Button createDepositButton() {
        Button generateButton = new Button("Deposit");
        generateButton.addClickListener(e -> {
        });
        return generateButton;
    }


    private void configureDepositTable() {

        depositTable.setColumnAlignments(Table.Align.LEFT, Table.Align.LEFT, Table.Align.RIGHT, Table.Align.LEFT, Table.Align.RIGHT, Table.Align.LEFT);

        depositTable.addGeneratedColumn("selected", (Table source, Object itemId, Object columnId) -> {

            DepositLineItem lineItem = (DepositLineItem)itemId;
            boolean enabled = !lineItem.getCurrentDue().isZero() && null != lineItem.getCheque();

            CheckBox checkBox = new CheckBox();
            checkBox.setValue(enabled);
            checkBox.setEnabled(enabled);

            return checkBox;
        });

    }


    public void refresh() {
        List<Loan> openLoans = loanService.findByCloseDateIsNull();
        List<DepositLineItem> depositCandidates = getDepositCandidates(openLoans);
        depositTable.setBeans(depositCandidates);
    }


    private List<DepositLineItem> getDepositCandidates(List<Loan> openLoans) {
        return openLoans.stream()
                .map(loan -> {
                    LoanStatus loanStatus = new LoanStatus(loan);
                    DepositLineItem depositItem = new DepositLineItem();
                    depositItem.setLoan(loan);
                    depositItem.setLoanName(loan.getLoanName());
                    depositItem.setRegularPayment(loanStatus.getRegularDue());
                    depositItem.setAsOf(loanStatus.getCurrentScheduledPaymentDate());
                    depositItem.setCurrentDue(loanStatus.getCurrentDue());
                    depositItem.setCheque(loanStatus.getNextChequeOnFile().orElse(null));
                    return depositItem;
                })
                .collect(Collectors.toList());
    }


}
