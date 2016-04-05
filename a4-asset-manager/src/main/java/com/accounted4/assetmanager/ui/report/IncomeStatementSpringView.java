package com.accounted4.assetmanager.ui.report;

import com.accounted4.assetmanager.UiRouter;
import com.accounted4.assetmanager.entity.Loan;
import com.accounted4.assetmanager.service.LoanService;
import com.accounted4.assetmanager.ui.loan.status.LoanStatus;
import com.accounted4.assetmanager.ui.loan.status.LoanStatusChargeLineItem;
import com.accounted4.assetmanager.ui.loan.status.LoanStatusLineItem;
import com.accounted4.assetmanager.ui.loan.status.LoanStatusPaymentLineItem;
import com.accounted4.assetmanager.util.vaadin.ui.DefaultView;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Button;
import com.vaadin.ui.Panel;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.TreeTable;
import com.vaadin.ui.themes.Reindeer;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.money.MonetaryAmount;
import org.javamoney.moneta.Money;
import org.vaadin.viritin.layouts.MFormLayout;
import org.vaadin.viritin.layouts.MVerticalLayout;


/**
 *
 * @author gheinze
 */
@UIScope
@SpringView(name = UiRouter.ViewName.INCOME_STATEMENT)
//@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class IncomeStatementSpringView extends Panel implements DefaultView {

    private static final long serialVersionUID = 1L;

    // TODO: pull currency from session
    private static final Money ZERO_MONEY = Money.of(BigDecimal.ZERO, "CAD");


    @Inject LoanService loanService;


    private TreeTable detailTable;
    private MVerticalLayout verticalLayout;

    private PopupDateField fromDatePopup;
    private PopupDateField toDatePopup;


    @PostConstruct
    private void init() {

        final int year = LocalDate.now().getYear();

        final Date defaultFromDate = Date.from(LocalDate.of(year, Month.JANUARY, 1).atStartOfDay(ZoneId.systemDefault()).toInstant());
        final Date defaultToDate   = Date.from(LocalDate.of(year, Month.DECEMBER, 31).atStartOfDay(ZoneId.systemDefault()).toInstant());

        fromDatePopup = createDateField("From Date");
        fromDatePopup.setValue(defaultFromDate);

        toDatePopup = createDateField("To Date");
        toDatePopup.setValue(defaultToDate);

        setCaption("Accrued Income");

        MFormLayout reportCriteriaForm = new MFormLayout(fromDatePopup, toDatePopup).withWidth("");

        Button generateButton = createGenerateButton();

        verticalLayout = new MVerticalLayout(reportCriteriaForm, generateButton);
        verticalLayout.setSizeFull();

        setContent(verticalLayout);
        addStyleName(Reindeer.PANEL_LIGHT);

    }


    private PopupDateField createDateField(String caption) {
        PopupDateField field = new PopupDateField(caption);
        field.setConverter(LocalDate.class);
        field.setDateFormat("dd-MMM-yyyy");
        field.setWidth("10em");
        field.setImmediate(true);
        return field;
    }


    private Button createGenerateButton() {
        Button generateButton = new Button("Generate");
        generateButton.addClickListener(e -> {
            if (null != detailTable) {
                verticalLayout.removeComponent(detailTable);
            }
            detailTable = getTable();
            verticalLayout.add(detailTable);
            verticalLayout.expand(detailTable);
            setSizeFull();
        });
        return generateButton;
    }



    private static final String FIELD_SEPARATOR = Character.toString((char)31);
    private static final String KEY_FORMAT = "%04d-%02d-%02d" + FIELD_SEPARATOR + "%s" + FIELD_SEPARATOR + "%s";


    private TreeTable getTable() {
        TreeMap<String, LoanStatusLineItem> orderedLineItems = getReportLineItems();
        return createTable(orderedLineItems);
    }


    private TreeMap<String, LoanStatusLineItem> getReportLineItems() {

        final LocalDate fromLocalDate = fromDatePopup.getValue().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        final LocalDate toLocalDate = toDatePopup.getValue().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        // Key for sorting items: date (yyyy-mm-dd), mortgage name, interest|fee
        final TreeMap<String, LoanStatusLineItem> orderedLineItems = new TreeMap<>();

        final List<Loan> loans = loanService.findAllLoans();

        loans.stream().forEach(loan -> {
            LoanStatus loanStatus = new LoanStatus(loan);
            loanStatus.getOrderedLineItems()
                    .stream()
                    .filter(loanLineItem -> (

                            // Don't report Payments
                            !(loanLineItem instanceof LoanStatusPaymentLineItem) &&

                            // Don't report Charges that are capitalizing (like tax, insurance, etc)
                            !(loanLineItem instanceof LoanStatusChargeLineItem &&
                                    ((LoanStatusChargeLineItem)loanLineItem).isCapitalizing()) &&

                            // Respect date ranges
                            !loanLineItem.getDate().isBefore(fromLocalDate) &&
                            !loanLineItem.getDate().isAfter(toLocalDate)
                                    ))

                    .forEach(loanLineItem -> {
                        String theKey = String.format(KEY_FORMAT
                                ,loanLineItem.getDate().getYear()
                                ,loanLineItem.getDate().getMonthValue()
                                ,loanLineItem.getDate().getDayOfMonth()
                                ,loan.getLoanName()
                                ,loanLineItem.getType().equals("Period Interest") ? "A" : "B"
                        );
                        orderedLineItems.put(theKey, loanLineItem);
                    });
        });

        return orderedLineItems;
    }



    private static final String TRANSACTION_PROPERTY = "Transaction";
    private static final String INTEREST_AMOUNT_PROPERTY = "Interest";
    private static final String FEE_AMOUNT_PROPERTY = "Fee";


    /*
     * Four levels of hierarchy:

     *     Total
     *         Date 1
     *             Mortgage 1
     *                 Detail 1 interest
     *                 Detail 2 fee
     *             Mortgage 2
     *                 Detail 1 interest
     *                 Detail 2 fee
     *         Date 2
     *             Mortgage 1
     *                 Detail 1 interest
     *                 Detail 2 fee
     *             Mortgage 2
     *                 Detail 1 interest
     *                 Detail 2 fee
     *
     */
    @SuppressWarnings("unchecked")
    private TreeTable createTable(TreeMap<String, LoanStatusLineItem> orderedLineItems) {

        TreeTable treeTable = new TreeTable();

        treeTable.setSizeFull();
        treeTable.setSelectable(true);

        // Define the table columns
        treeTable.addContainerProperty(TRANSACTION_PROPERTY, String.class, "");
        treeTable.addContainerProperty(INTEREST_AMOUNT_PROPERTY, MonetaryAmount.class, ZERO_MONEY);
        treeTable.addContainerProperty(FEE_AMOUNT_PROPERTY, MonetaryAmount.class, ZERO_MONEY);

        // First level totals (Grand total)
        Money totalInterest = ZERO_MONEY;
        Money totalFees = ZERO_MONEY;

        // Second level totals (Date)
        Money transactionDateInterest = ZERO_MONEY;
        Money transactionDateFees = ZERO_MONEY;

        // Third level totals (Mortgage)
        Money loanInterest = ZERO_MONEY;
        Money loanFees = ZERO_MONEY;

        // Initialize triggers to indicate hierarchy change
        LocalDate lastTransactionDate = fromDatePopup.getValue().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().minusDays(1L);
        String lastLoanName = "";

        // Root table item: Level 1
        final Object rootTableRowId = treeTable.addItem(new Object[] {"Total", ZERO_MONEY, ZERO_MONEY }, null);
        treeTable.setCollapsed(rootTableRowId, false);

        Object transactionDateRowId = null;  // Level 2 table row
        Object loanRowId = null;             // Leval 3 table row

        for (Entry<String, LoanStatusLineItem> entry : orderedLineItems.entrySet()) {

            LoanStatusLineItem loanLineItem = entry.getValue();
            String key = entry.getKey();

            boolean dateChanged = loanLineItem.getDate().isAfter(lastTransactionDate);

            if (dateChanged) {
                // New Transaction date forces a new Level 2 subheading row
                transactionDateRowId = treeTable.addItem(new Object[] { loanLineItem.getDate().toString(), transactionDateInterest, transactionDateFees }, null);
                treeTable.setParent(transactionDateRowId, rootTableRowId);
                treeTable.setCollapsed(transactionDateRowId, false);
                transactionDateInterest = ZERO_MONEY;
                transactionDateFees = ZERO_MONEY;
                lastTransactionDate = loanLineItem.getDate();
            }

            String loanName = key.split(FIELD_SEPARATOR)[1];
            boolean loanChanged = !loanName.equals(lastLoanName);

            if (dateChanged || loanChanged) {
                // New Loan name subheading forces a new Level 3 subheading row
                loanRowId = treeTable.addItem(new Object[] { loanName, loanInterest, loanFees }, null);
                treeTable.setParent(loanRowId, transactionDateRowId);
                treeTable.setCollapsed(loanRowId, true);
                loanInterest = ZERO_MONEY;
                loanFees = ZERO_MONEY;
                lastLoanName = loanName;
            }

            // Detail entry
            boolean isInterest = loanLineItem.getType().equals("Period Interest");
            MonetaryAmount itemInterest = isInterest ? loanLineItem.getTransaction() : ZERO_MONEY;
            MonetaryAmount itemFee = isInterest ? ZERO_MONEY : loanLineItem.getFees();

            if (itemInterest.isZero() && itemFee.isZero()) {
                continue;
            }

            // Add the detail row to the table
            final Object itemId = treeTable.addItem(new Object[] { loanLineItem.getType(), itemInterest, itemFee }, null);
            treeTable.setParent(itemId, loanRowId);
            treeTable.setChildrenAllowed(itemId, false);
            treeTable.setCollapsed(itemId, false);


            // Update all the counters

            loanInterest = loanInterest.add(itemInterest);
            loanFees = loanFees.add(itemFee);

            transactionDateInterest = transactionDateInterest.add(itemInterest);
            transactionDateFees = transactionDateFees.add(itemFee);

            totalInterest = totalInterest.add(itemInterest);
            totalFees = totalFees.add(itemFee);

            // Update all the totalling rows in the tree with the new counter values
            // TODO: really we only want to do this once at hierarchy switch rather than on each item...
            treeTable.getItem(loanRowId).getItemProperty(INTEREST_AMOUNT_PROPERTY).setValue(loanInterest);
            treeTable.getItem(loanRowId).getItemProperty(FEE_AMOUNT_PROPERTY).setValue(loanFees);

            treeTable.getItem(transactionDateRowId).getItemProperty(INTEREST_AMOUNT_PROPERTY).setValue(transactionDateInterest);
            treeTable.getItem(transactionDateRowId).getItemProperty(FEE_AMOUNT_PROPERTY).setValue(transactionDateFees);

            treeTable.getItem(rootTableRowId).getItemProperty(INTEREST_AMOUNT_PROPERTY).setValue(totalInterest);
            treeTable.getItem(rootTableRowId).getItemProperty(FEE_AMOUNT_PROPERTY).setValue(totalFees);

        }

        treeTable.setSizeFull();

        return treeTable;

    }


}