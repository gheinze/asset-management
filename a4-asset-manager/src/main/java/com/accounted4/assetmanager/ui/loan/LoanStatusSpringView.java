package com.accounted4.assetmanager.ui.loan;

import com.accounted4.assetmanager.ui.loan.status.LoanStatusLineItem;
import com.accounted4.assetmanager.ui.loan.status.LoanStatus;
import com.accounted4.assetmanager.service.LoanService;
import com.accounted4.assetmanager.repository.LoanRepository;
import com.accounted4.assetmanager.entity.Loan;
import com.accounted4.assetmanager.util.vaadin.ui.AmMTable;
import com.accounted4.assetmanager.util.vaadin.ui.DefaultView;
import com.accounted4.assetmanager.util.vaadin.ui.Refreshable;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Page;
import com.vaadin.server.StreamResource;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Button;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Table;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import net.sf.jasperreports.engine.JRException;
import org.slf4j.LoggerFactory;
import org.vaadin.viritin.fields.MTable;
import org.vaadin.viritin.layouts.MVerticalLayout;

/**
 *
 * @author gheinze
 */
@UIScope
@SpringView
public class LoanStatusSpringView extends MVerticalLayout implements DefaultView, Refreshable {

    private final String[] fieldNames = {
        "date",
        "scheduledAmount", "scheduledInterest", "scheduledPrincipal", "scheduledBalance",
        "transaction", "balance", "fees",
        "type", "note"
    };

    private final MTable<LoanStatusLineItem> transactionTable = new AmMTable<>(LoanStatusLineItem.class)
            .withProperties(fieldNames)
            .withColumnHeaders(fieldNames)
            .setSortableProperties("date")
            .withFullWidth()
            ;

    private final LoanService loanService;
    private final LoanRepository loanRepo;
    private Loan selectedLoan;

    private LoanStatusSummaryPanel loanSummaryPanel;
    private Button generatePdfButton;


    @Inject
    public LoanStatusSpringView(LoanService loanService, LoanRepository loanRepo) {
        this.loanService = loanService;
        this.loanRepo = loanRepo;
    }


    @PostConstruct
    public void init() {

        configureGeneratePdfButton();

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

        loanSummaryPanel = new LoanStatusSummaryPanel();
        addComponent(new MVerticalLayout(generatePdfButton, loanSummaryPanel, transactionTable).expand(transactionTable));
        withFullWidth();
        withFullHeight();
    }

    private void configureGeneratePdfButton() {
        generatePdfButton = new Button("pdf");
        generatePdfButton.setIcon(FontAwesome.FILE_PDF_O);
        generatePdfButton.addStyleName("redicon");
        generatePdfButton.setDescription("Generate PDF schedule");
        generatePdfButton.addClickListener(e -> {
            displayPdfSchedule();
        });
    }


    private void refreshTables() {
        LoanStatus status = new LoanStatus(selectedLoan);
        loanSummaryPanel.refresh(status);
        refreshTransactionTable(status);
    }


    private void refreshTransactionTable(LoanStatus status) {
        transactionTable.setBeans(status.getOrderedLineItems());
        String[] sortColumns = {"date"};
        boolean[] sortDirections = {true};
        transactionTable.sort(sortColumns, sortDirections);
    }


    private void displayPdfSchedule() {

        StreamResource.StreamSource pdfStreamFromServer = () -> {
            try {
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                loanService.writePdfLoanStatusToStream(selectedLoan, outputStream);
                byte[] data = outputStream.toByteArray();
                ByteArrayInputStream inputStream = new ByteArrayInputStream(data);
                return inputStream;
            } catch (JRException | IOException ex) {
                // TODO: slf4j logging
            LoggerFactory
                    .getLogger(LoanStatusSpringView.class)
                    .warn("Pdf generation failed", ex);
                    new Notification("Error generating pdf schedule", "", Notification.Type.WARNING_MESSAGE, true).show(Page.getCurrent());
            }
            return null;
        };

        final Window window = new PdfScheduleWindow(pdfStreamFromServer);
        UI.getCurrent().addWindow(window);

    }


    // ====================
    // == Exposed API
    // ====================

    public void setLoan(Loan selectedLoan) {
        this.selectedLoan = selectedLoan;
        refreshTables();
    }

    @Override
    public void refresh() {
        selectedLoan = loanRepo.findOne(selectedLoan.getId());
        refreshTables();
    }

}
