package com.accounted4.assetmanager.finance.loan;

import com.accounted4.assetmanager.VaadinUI;
import com.accounted4.assetmanager.util.vaadin.converter.FieldGroupFactory;
import com.accounted4.assetmanager.util.vaadin.converter.MonetaryAmountConverter;
import com.accounted4.finance.loan.AmortizationAttributes;
import com.accounted4.finance.loan.AmortizationCalculator;
import com.accounted4.finance.loan.ScheduledPayment;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.Page;
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
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import javax.money.MonetaryAmount;
import org.javamoney.moneta.Money;


/**
 *
 * @author gheinze
 */
public class PaymentScheduleCalculator extends Panel {

    private static final String INTEREST_ONLY_PROPERTY_ID = "interestOnly";
    private static final String REGULAR_PAYMENT_PROPERTY_ID = "regularPayment";


    private final LoanService loanService;
    private final BeanFieldGroup<AmortizationAttributes> amAttrBinder;
    private final Panel amortizationPanel;
    private Button generateButton;


    public PaymentScheduleCalculator(LoanService loanService) {
        this.loanService = loanService;
        amAttrBinder = createDataModel(getDefaultAmortizationAttributesTemplate());
        amortizationPanel = createAmortizationPanel(amAttrBinder);
        setupMainPanel();
        wireListeners();
    }


    private BeanFieldGroup<AmortizationAttributes> createDataModel(AmortizationAttributes amAttrs) {
        BeanFieldGroup<AmortizationAttributes> binder = new BeanFieldGroup<>(AmortizationAttributes.class);
        binder.setFieldFactory(new FieldGroupFactory());
        binder.setItemDataSource(amAttrs);
        return binder;
    }


    private Panel createAmortizationPanel(BeanFieldGroup<AmortizationAttributes> binder) {

        GridLayout layout = new GridLayout(2, 2);
        layout.setSpacing(true);

        TextField amMonthsField = (TextField) binder.buildAndBind("Amortization months", "amortizationPeriodInMonths");
        layout.addComponent(amMonthsField, 0, 1);

        ComboBox compoundingPeriodComboBox = CompoundingPeriodComboBox.create(binder);
        layout.addComponent(compoundingPeriodComboBox, 1, 1);

        Panel subPanel = new Panel();
        subPanel.setSizeUndefined();
        subPanel.setContent(layout);
        subPanel.addStyleName(Reindeer.PANEL_LIGHT);
        subPanel.setEnabled(true);

        return subPanel;

    }


    private void setupMainPanel() {
        VerticalLayout masterPanelLayout = new VerticalLayout();
        masterPanelLayout.addComponent(getLoanDetailPanel());
        Panel footerPanel = getFooter();
        masterPanelLayout.addComponent(footerPanel);
        masterPanelLayout.setComponentAlignment(footerPanel, Alignment.MIDDLE_CENTER);
        Panel masterPanel = new Panel();
        masterPanel.setContent(masterPanelLayout);
        setContent(masterPanel);
    }


    private Panel getLoanDetailPanel() {

        FormLayout loanDetailFormLayout = new FormLayout();
        Panel loanDetailPanel = new Panel("Loan Details");
        loanDetailPanel.setContent(loanDetailFormLayout);
        loanDetailPanel.setSizeUndefined();
        loanDetailPanel.addStyleName(Reindeer.PANEL_LIGHT); // remove border from panel

        loanDetailFormLayout.setCaption("Payment Schedule Calculator");
        loanDetailFormLayout.setMargin(true);
        loanDetailFormLayout.setSpacing(true);
        loanDetailFormLayout.addComponent(createStartDateField());
        loanDetailFormLayout.addComponent(amAttrBinder.buildAndBind("Term in months", "termInMonths"));
        loanDetailFormLayout.addComponent(amAttrBinder.buildAndBind("Interest only", INTEREST_ONLY_PROPERTY_ID));
        loanDetailFormLayout.addComponent(amortizationPanel);
        loanDetailFormLayout.addComponent(createLoanAmountField());
        loanDetailFormLayout.addComponent(amAttrBinder.buildAndBind("Interest rate %", "interestRateAsPercent"));
        loanDetailFormLayout.addComponent(PaymentPeriodComboBox.create(amAttrBinder));
        loanDetailFormLayout.addComponent(createPaymentField());

        return loanDetailPanel;

    }


    private Panel getFooter() {

        HorizontalLayout footerLayout = new HorizontalLayout();
        generateButton = new Button("Generate Schedule");
        generateButton.addClickListener(e -> {
            displaySchedule();
        });
        footerLayout.addComponent(generateButton);
        footerLayout.setComponentAlignment(generateButton, Alignment.MIDDLE_CENTER);

        Panel footerPanel = new Panel();
        footerPanel.setContent(footerLayout);
        footerPanel.addStyleName(Reindeer.PANEL_LIGHT);

        return footerPanel;

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


    private AmortizationAttributes getDefaultAmortizationAttributesTemplate() {

        AmortizationAttributes amAttrs = new AmortizationAttributes();

        LocalDate adjustedDate = AmortizationCalculator.getNextFirstOrFifteenthOfTheMonth(LocalDate.now());
        amAttrs.setAdjustmentDate(adjustedDate);
        amAttrs.setStartDate(adjustedDate);

        amAttrs.setTermInMonths(12);
        amAttrs.setAmortizationPeriodInMonths(240);
        amAttrs.setInterestRateAsPercent(10.);

        // TODO: Currency from session
        Locale locale = (Locale)VaadinSession.getCurrent().getAttribute(VaadinUI.LOCALE_KEY);
        String sessionCurrencyCode = NumberFormat.getCurrencyInstance(locale).getCurrency().getCurrencyCode();
        amAttrs.setLoanAmount(Money.of(20000, sessionCurrencyCode));
        amAttrs.setRegularPayment(Money.of(BigDecimal.ZERO, sessionCurrencyCode));

        return amAttrs;
    }


    private PaymentField createPaymentField() {
        PaymentField paymentField = new PaymentField();
        paymentField.setCaption("Regular payment");
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
            amAttrBinder.commit();
            AmortizationAttributes amAttrs = amAttrBinder.getItemDataSource().getBean();
            MonetaryAmount regularPayment = loanService.getPeriodicPayment(amAttrs);
            List<ScheduledPayment> generatedSchedule = loanService.generateSchedule(amAttrs);
            grid.setContainerDataSource(new BeanItemContainer<>(ScheduledPayment.class, generatedSchedule));
            grid.setColumnOrder("paymentNumber", "paymentDate", "payment", "interest", "principal", "balance");
            UI.getCurrent().addWindow(window);
        } catch (FieldGroup.CommitException ex) {
            // TODO: slf4j logging
            new Notification("Please correct form field errors before generating a schedule", "", Notification.Type.WARNING_MESSAGE, true).show(Page.getCurrent());
        }
    }


    // Create a composite field showing a TextField for the regularPayment with a Button beside it to calculate
    private class PaymentField extends CustomField<MonetaryAmount> {

        private final MonetaryAmountConverter monetaryAmountConverter = new MonetaryAmountConverter();
        private final TextField field = new TextField();

        @Override
        protected Component initContent() {

            HorizontalLayout layout = new HorizontalLayout();

            field.setImmediate(true);
            field.setConverter(MonetaryAmount.class);

            Button calculateButton = new Button("calculate");
            calculateButton.addClickListener((Button.ClickEvent e) -> {
                try {
                    amAttrBinder.commit();
                    AmortizationAttributes amAttrs = amAttrBinder.getItemDataSource().getBean();
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

            Panel panel = new Panel();
            panel.addStyleName(Reindeer.PANEL_LIGHT); // remove border from panel
            panel.setSizeUndefined();
            panel.setContent(layout);
            return panel;
        }

        @Override
        public Class<? extends MonetaryAmount> getType() {
            return MonetaryAmount.class;
        }

        private Field getInternal() {
            return field;
        }
    }

    private void wireListeners() {
        wireAmortizationAttributesToListenToInterestOnlyCheckBox();
        wireGenerateButtonToValidForm();
    }

    private void wireAmortizationAttributesToListenToInterestOnlyCheckBox() {
        CheckBox interestOnlyCheckBox = (CheckBox)amAttrBinder.getField(INTEREST_ONLY_PROPERTY_ID);
        interestOnlyCheckBox.addValueChangeListener(e -> amortizationPanel.setEnabled(!(Boolean)e.getProperty().getValue()));
    }

    private void wireGenerateButtonToValidForm() {
        amAttrBinder.getFields().stream().forEach((f) -> {
            f.addValueChangeListener(e -> { formHasChanged(); });
        });
    }

    private void formHasChanged() {
        boolean allFieldsValid = amAttrBinder.isValid();
        generateButton.setEnabled(allFieldsValid);
    }

}