package com.accounted4.assetmanager.loan;

import com.accounted4.assetmanager.UiRouter;
import com.accounted4.assetmanager.util.vaadin.ui.DefaultView;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Panel;

/**
 *
 * @author gheinze
 */
@UIScope
@SpringView(name = UiRouter.ViewName.LOANS)
public class LoanPanel extends Panel implements DefaultView {

    public LoanPanel() {
        super("Loans");
    }

}
