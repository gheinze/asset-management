package com.accounted4.assetmanager;

import com.google.gwt.thirdparty.guava.common.base.CaseFormat;
import lombok.Getter;

/**
 * Name the menu items for navigation. Should be listed in hierarchical order (i.e. parent always
 * defined before child). This helps dynamically building the menu.
 *
 * @author gheinze
 */
public enum UiRouter {

     MortgageAdministration(null, ViewName.MORTGAGE_ADMINISTRATION)
    ,PaymentCalculator(MortgageAdministration, ViewName.PAYMENT_CALCULATOR)

    ;


    @Getter private final UiRouter parent;
    @Getter private final String viewName;
    @Getter private final String displayName;

    private UiRouter(UiRouter parent, String viewName) {
        this.parent = parent;
        this.viewName = viewName;
        this.displayName = viewName.replace("_", " ");
    }

    // To allow usage within annotation
    public interface ViewName {
        String MORTGAGE_ADMINISTRATION = "Mortgage_Administration";
        String PAYMENT_CALCULATOR = "Payment_Calculator";
    }


}
