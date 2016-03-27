package com.accounted4.assetmanager.ui.loan;

import com.accounted4.finance.loan.AmortizationAttributes;
import com.accounted4.finance.loan.TimePeriod;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.ui.ComboBox;
import java.util.Arrays;

/**
 *
 * @author gheinze
 */
public class PaymentPeriodComboBox {


    public static ComboBox create(BeanFieldGroup<AmortizationAttributes> binder) {
        BeanContainer<Integer, TimePeriod> periodContainer = getCompoundingPeriodBeanContainer();

        ComboBox paymentFrequency = new ComboBox("Payment frequency", periodContainer);
        paymentFrequency.setNullSelectionAllowed(false);
        paymentFrequency.setTextInputAllowed(false);
        paymentFrequency.setItemCaptionPropertyId("displayName");

        binder.bind(paymentFrequency, "paymentFrequency");

        return paymentFrequency;

    }


    private static BeanContainer<Integer, TimePeriod> getCompoundingPeriodBeanContainer() throws IllegalStateException {
        BeanContainer<Integer, TimePeriod> compoundingPeriodContainer = new BeanContainer<>(TimePeriod.class);
        compoundingPeriodContainer.setBeanIdProperty("periodsPerYear");
        compoundingPeriodContainer.addAll(Arrays.asList(TimePeriod.values()));
        return compoundingPeriodContainer;
    }

}
