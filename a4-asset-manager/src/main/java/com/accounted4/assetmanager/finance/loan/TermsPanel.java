package com.accounted4.assetmanager.finance.loan;

import com.accounted4.assetmanager.VaadinUI;
import com.accounted4.assetmanager.util.vaadin.converter.FieldGroupFactory;
import com.accounted4.assetmanager.util.vaadin.converter.MonetaryAmountConverter;
import com.accounted4.assetmanager.util.vaadin.ui.BorderlessPanel;
import com.accounted4.finance.loan.AmortizationAttributes;
import com.accounted4.finance.loan.ScheduledPayment;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Page;
import com.vaadin.server.StreamResource;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomField;
import com.vaadin.ui.Field;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Grid;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.Reindeer;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.money.MonetaryAmount;
import net.sf.jasperreports.engine.JRException;
import org.javamoney.moneta.Money;
import org.slf4j.LoggerFactory;

/**
 * A panel holding the terms of a loan used to calculate the expected periodic payments for the loan.
 * Includes a buttons for generating a schedule and calculating a regular payment. This panel is shared
 * between forms (ex Mortgage calculator and Loan details).
 *
 * @author gheinze
 */
public class TermsPanel extends Panel {

    private static final long serialVersionUID = 1L;

    private static final String INTEREST_ONLY_PROPERTY_ID = "interestOnly";
    private static final String REGULAR_PAYMENT_PROPERTY_ID = "regularPayment";

    private final LoanService loanService;

    private BeanFieldGroup<AmortizationAttributes> amAttrBinder;
    private GridLayout amortizationComponent;
    private Button generateButton;
    private Button generatePdfButton;
    private Button calculateButton;

    private final ArrayList<ValueChangeListener> formChangeListneres = new ArrayList<>();


    public TermsPanel(LoanService loanService) {
        this(loanService, getDefaultAmortizationAttributes());
    }

    public TermsPanel(LoanService loanService, AmortizationAttributes amAttr) {
        this.loanService = loanService;
        this.amAttrBinder = createDataModel(amAttr);
        init();
    }

    private void init() {
        amortizationComponent = createAmortizationComponent(amAttrBinder);
        setContent(getContentArea());
        wireListeners();
        setSizeUndefined();
        addStyleName(Reindeer.PANEL_LIGHT);
    }


    private BeanFieldGroup<AmortizationAttributes> createDataModel(AmortizationAttributes amAttrs) {
        BeanFieldGroup<AmortizationAttributes> binder = new BeanFieldGroup<>(AmortizationAttributes.class);
        binder.setFieldFactory(new FieldGroupFactory());
        binder.setItemDataSource(amAttrs);
        return binder;
    }


    /*
     * The compound component holding the Amortization Months and the Compounding Period components.
    */
    private GridLayout createAmortizationComponent(BeanFieldGroup<AmortizationAttributes> binder) {

        GridLayout layout = new GridLayout(2, 2);
        layout.setSpacing(true);

        TextField amMonthsField = (TextField) binder.buildAndBind("Amortization months", "amortizationPeriodInMonths");
        amMonthsField.setDescription(getMonthToYearMappingDescription());
        layout.addComponent(amMonthsField, 0, 1);

        ComboBox compoundingPeriodComboBox = CompoundingPeriodComboBox.create(binder);
        layout.addComponent(compoundingPeriodComboBox, 1, 1);
        layout.setEnabled(true);

        return layout;

    }


    // Vertical components:
    //    o terms
    //    o action buttons
    private VerticalLayout getContentArea() {
        VerticalLayout contentAreaLayout = new VerticalLayout();
        contentAreaLayout.addComponent(getLoanDetailPanel());
        HorizontalLayout contentAreaFooter = getGenerateButtonRow();
        contentAreaLayout.addComponent(contentAreaFooter);
        contentAreaLayout.setComponentAlignment(contentAreaFooter, Alignment.MIDDLE_CENTER);
        contentAreaLayout.setSizeUndefined();
        return contentAreaLayout;
    }


    private Panel getLoanDetailPanel() {

        FormLayout loanDetailFormLayout = new FormLayout();
        loanDetailFormLayout.setSizeUndefined();

        loanDetailFormLayout.setCaption("Payment Schedule Calculator");
        loanDetailFormLayout.setSpacing(true);
        loanDetailFormLayout.addComponent(createStartDateField());
        loanDetailFormLayout.addComponent(creatTermField());
        loanDetailFormLayout.addComponent(amAttrBinder.buildAndBind("Interest only", INTEREST_ONLY_PROPERTY_ID));
        loanDetailFormLayout.addComponent(amortizationComponent);
        loanDetailFormLayout.addComponent(createLoanAmountField());
        loanDetailFormLayout.addComponent(amAttrBinder.buildAndBind("Interest rate %", "interestRateAsPercent"));
        loanDetailFormLayout.addComponent(PaymentPeriodComboBox.create(amAttrBinder));
        loanDetailFormLayout.addComponent(createPaymentField());

        Panel loanDetailPanel = BorderlessPanel.create("Loan Details");
        loanDetailPanel.setContent(loanDetailFormLayout);
        loanDetailPanel.setSizeUndefined();

        return loanDetailPanel;

    }


    /*
     * The buttons to process the form.
    */
    private HorizontalLayout getGenerateButtonRow() {

        HorizontalLayout footerLayout = new HorizontalLayout();

        configureGenerateButton();
        footerLayout.addComponent(generateButton);
        footerLayout.setComponentAlignment(generateButton, Alignment.MIDDLE_CENTER);

        configureGeneratePdfButton();
        footerLayout.addComponent(generatePdfButton);

        return footerLayout;

    }


    private void configureGenerateButton() {
        generateButton = new Button("Generate Schedule");
        generateButton.setIcon(FontAwesome.TABLE);
        generateButton.setDescription("Generate a tabular schedule");
        generateButton.addClickListener(e -> {
            displaySchedule();
        });
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


    private TextField creatTermField() {
        TextField field = (TextField)amAttrBinder.buildAndBind("Term in months", "termInMonths");
        field.setDescription(getMonthToYearMappingDescription());
        return field;
    }

    private PopupDateField createStartDateField() {
        PopupDateField field = amAttrBinder.buildAndBind("Start Date", "startDate", PopupDateField.class);
        field.setConverter(LocalDate.class);
        field.setImmediate(true);
        return field;
    }


    private TextField createLoanAmountField() {
        TextField field = (TextField) amAttrBinder.buildAndBind("Loan amount", "loanAmount");
        field.setImmediate(true);
        field.setConverter(MonetaryAmount.class);
        return field;
    }


    private PaymentField createPaymentField() {
        calculateButton = new Button();
        PaymentField paymentField = new PaymentField();
        paymentField.setCaption("Regular payment");
        paymentField.setDescription("You can override the calculated amount for extra principal payment");
        paymentField.setSizeUndefined();
        paymentField.setImmediate(true);
        amAttrBinder.bind(paymentField.getInternal(), REGULAR_PAYMENT_PROPERTY_ID);
        return paymentField;
    }

    private void displaySchedule() {

        final Window window = new Window("Schedule");
        window.setSizeUndefined();
        final Grid grid = new Grid();
        window.setContent(grid);
        window.center();

        try {
            AmortizationAttributes amAttrs = flushFormAndRetrieveModel();
            List<ScheduledPayment> generatedSchedule = loanService.generateSchedule(amAttrs);
            grid.setContainerDataSource(new BeanItemContainer<>(ScheduledPayment.class, generatedSchedule));
            grid.setColumnOrder("paymentNumber", "paymentDate", "payment", "interest", "principal", "balance");
            UI.getCurrent().addWindow(window);
        } catch (FieldGroup.CommitException ex) {
            // TODO: slf4j logging
            new Notification("Please correct form field errors before generating a schedule", "", Notification.Type.WARNING_MESSAGE, true).show(Page.getCurrent());
        }
    }

    private AmortizationAttributes flushFormAndRetrieveModel() throws FieldGroup.CommitException {
        amAttrBinder.commit();
        AmortizationAttributes amAttrs = amAttrBinder.getItemDataSource().getBean();
        // We don't use adjustment date in this ui, but schedules are based on adjusment date, not start date.
        amAttrs.setAdjustmentDate(amAttrs.getStartDate());
        return amAttrs;
    }



    private void displayPdfSchedule() {

        StreamResource.StreamSource pdfStreamFromServer = () -> {
            try {
                AmortizationAttributes amAttrs = flushFormAndRetrieveModel();
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                loanService.writePdfScheduleToStream(amAttrs, outputStream);
                byte[] data = outputStream.toByteArray();
                ByteArrayInputStream inputStream = new ByteArrayInputStream(data);
                return inputStream;
            } catch (FieldGroup.CommitException | JRException | IOException ex) {
                // TODO: slf4j logging
                new Notification("Error generating pdf schedule", "", Notification.Type.WARNING_MESSAGE, true).show(Page.getCurrent());
            }
            return null;
        };

        if (null == pdfStreamFromServer) {
            return;
        }

        final Window window = new PdfScheduleWindow(pdfStreamFromServer);
        UI.getCurrent().addWindow(window);

    }

    private String getMonthToYearMappingDescription() {
        return "<table style='float:right'>"
                + "<tr><th align='right'>Years</th><th align='right'>Months</th></tr>"
                + "<tr><td align='right'>5</td><td align='right'>60</td></tr>"
                + "<tr><td align='right'>10</td><td align='right'>120</td></tr>"
                + "<tr><td align='right'>15</td><td align='right'>180</td></tr>"
                + "<tr><td align='right'>20</td><td align='right'>240</td></tr>"
                + "<tr><td align='right'>25</td><td align='right'>300</td></tr>"
                + "</table>"
        ;
    }


    private void wireListeners() {
        wireAmortizationAttributesToListenToInterestOnlyCheckBox();
        wireGenerateButtonToValidForm();
    }

    private void wireAmortizationAttributesToListenToInterestOnlyCheckBox() {
        CheckBox interestOnlyCheckBox = (CheckBox)amAttrBinder.getField(INTEREST_ONLY_PROPERTY_ID);
        interestOnlyCheckBox.addValueChangeListener(e -> amortizationComponent.setEnabled(!(Boolean)e.getProperty().getValue()));
    }

    private void wireGenerateButtonToValidForm() {
        amAttrBinder.getFields().stream().forEach((f) -> {
            f.addValueChangeListener(e -> { formHasChanged(); });
        });
    }

    private void formHasChanged() {
        boolean allFieldsValid = amAttrBinder.isValid();
        generateButton.setEnabled(allFieldsValid);
        generatePdfButton.setEnabled(allFieldsValid);
        calculateButton.setEnabled(allFieldsValid);
        if (allFieldsValid) {
            try {
                AmortizationAttributes updatedAmAttr = flushFormAndRetrieveModel();
                fireFormChangeListeners(updatedAmAttr);
            } catch (FieldGroup.CommitException ex) {
                LoggerFactory
                        .getLogger(TermsPanel.class)
                        .warn("Failed firing terms form changed listeners", ex);
            }
        }
    }

    private void fireFormChangeListeners(AmortizationAttributes updatedAmAttr) {
        final AmortizationAttributesPropertyWrapper wrappedAmAttrs =
                new AmortizationAttributesPropertyWrapper(updatedAmAttr);
        formChangeListneres.stream().forEach((listener) -> {
           listener.valueChange(() -> wrappedAmAttrs);
        });
    }

    public void addFormChangeListner(ValueChangeListener valueChangeListener) {
        formChangeListneres.add(valueChangeListener);
    }


    // ---------------------------------

    // Create a composite field showing a TextField for the regularPayment with a Button beside it to calculate
    private class PaymentField extends CustomField<MonetaryAmount> {

        private final MonetaryAmountConverter monetaryAmountConverter = new MonetaryAmountConverter();
        private final TextField field = new TextField();


        @Override
        protected Component initContent() {

            HorizontalLayout layout = new HorizontalLayout();

            field.setImmediate(true);
            field.setConverter(MonetaryAmount.class);

            calculateButton.setCaption("calculate");
            calculateButton.setIcon(FontAwesome.DOLLAR);
            calculateButton.addStyleName("greenicon");
            calculateButton.setDescription("Calculate periodic payment");

            calculateButton.addClickListener((Button.ClickEvent e) -> {
                try {
                    AmortizationAttributes amAttrs = flushFormAndRetrieveModel();
                    MonetaryAmount regularPayment = loanService.getPeriodicPayment(amAttrs);
                    field.setValue(monetaryAmountConverter.convertToPresentation(regularPayment, String.class, null));
                    amAttrBinder.commit();
                } catch (FieldGroup.CommitException ex) {
                    // TODO: slf4j logging
                    new Notification("Please correct form fields before calculating", "", Notification.Type.WARNING_MESSAGE, true).show(Page.getCurrent());
                }
            });

            layout.addComponent(field);
            layout.addComponent(calculateButton);

            return layout;
        }

        @Override
        public Class<? extends MonetaryAmount> getType() {
            return MonetaryAmount.class;
        }

        private Field getInternal() {
            return field;
        }
    }


    public static AmortizationAttributes getDefaultAmortizationAttributes() {
       // TODO: Currency from session
        Locale locale = (Locale)VaadinSession.getCurrent().getAttribute(VaadinUI.LOCALE_KEY);
        String sessionCurrencyCode = NumberFormat.getCurrencyInstance(locale).getCurrency().getCurrencyCode();
        Money defaultLoanAmount = Money.of(20000, sessionCurrencyCode);
        return AmortizationAttributes.getDefaultInstance(defaultLoanAmount);
    }



    // ---------------------------------

    // A Vaadin "Property" wrapping AmortizationAttributes
    private static class AmortizationAttributesPropertyWrapper implements Property<AmortizationAttributes> {

        private final AmortizationAttributes amAttr;

        public AmortizationAttributesPropertyWrapper(AmortizationAttributes amAttr) {
            this.amAttr = amAttr;
        }

        @Override
        public AmortizationAttributes getValue() {
            return amAttr;
        }

        @Override
        public void setValue(AmortizationAttributes newValue) throws Property.ReadOnlyException {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public Class<? extends AmortizationAttributes> getType() {
            return AmortizationAttributes.class;
        }

        @Override
        public boolean isReadOnly() {
            return true;
        }

        @Override
        public void setReadOnly(boolean newStatus) {
            throw new UnsupportedOperationException("Not supported.");
        }
    }

}
