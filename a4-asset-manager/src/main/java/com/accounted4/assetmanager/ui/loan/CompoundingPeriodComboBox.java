package com.accounted4.assetmanager.ui.loan;

import com.accounted4.finance.loan.AmortizationAttributes;
import com.accounted4.finance.loan.TimePeriod;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.ui.ComboBox;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 *
 * @author gheinze
 */
public class CompoundingPeriodComboBox {


    public static ComboBox create(BeanFieldGroup<AmortizationAttributes> binder) {
        BeanContainer<Integer, TimePeriod> compoundingPeriodContainer = getCompoundingPeriodBeanContainer();

        ComboBox compoundingPeriod = new ComboBox("Compounded", compoundingPeriodContainer);
        compoundingPeriod.setNullSelectionAllowed(false);
        compoundingPeriod.setTextInputAllowed(false);
        compoundingPeriod.setItemCaptionPropertyId("displayName");

        binder.bind(compoundingPeriod, "compoundingPeriodsPerYear");
        //compoundingPeriod.select(TimePeriod.SemiAnnually.getPeriodsPerYear()); // called after bind

        return compoundingPeriod;

    }


    private static BeanContainer<Integer, TimePeriod> getCompoundingPeriodBeanContainer() throws IllegalStateException {
        BeanContainer<Integer, TimePeriod> compoundingPeriodContainer = new BeanContainer<>(TimePeriod.class);
        compoundingPeriodContainer.setBeanIdProperty("periodsPerYear");
        compoundingPeriodContainer.addAll(
                Arrays.stream(TimePeriod.values())
                        .filter(period -> period.isCompoundingPeriod())
                        .collect(Collectors.toList())
        );
        return compoundingPeriodContainer;
    }

}
