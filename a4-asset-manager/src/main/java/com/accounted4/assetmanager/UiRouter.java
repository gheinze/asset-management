package com.accounted4.assetmanager;

import lombok.Getter;

/**
 * Name the menu items for navigation. Should be listed in hierarchical order (i.e. parent always
 * defined before child). This helps dynamically building the menu.
 *
 * @author gheinze
 */
public enum UiRouter {

     MortgageAdministration(null, ViewName.MORTGAGE_ADMINISTRATION, false)
    ,PaymentCalculator(MortgageAdministration, ViewName.PAYMENT_CALCULATOR)
    ,Parties(MortgageAdministration, ViewName.PARTIES)
    ,Loans(MortgageAdministration, ViewName.LOANS)

    ;


    @Getter private final UiRouter parent;
    @Getter private final String viewName;
    @Getter private final String displayName;
    @Getter private final boolean navigable;

    private UiRouter(UiRouter parent, String viewName) {
       this(parent, viewName, true);
    }

    private UiRouter(UiRouter parent, String viewName, boolean navigable) {
        this.parent = parent;
        this.viewName = viewName;
        this.displayName = viewName.replace("_", " ");
        this.navigable = navigable;
    }

    // To allow usage within annotation
    // The displayName currently needs to be unique since it is the id of the menu's value change event
    public interface ViewName {
        String MORTGAGE_ADMINISTRATION = "Mortgage_Administration";
        String PAYMENT_CALCULATOR = "Payment_Calculator";
        String PARTIES = "Parties";
        String LOANS = "Loans";
    }


}
