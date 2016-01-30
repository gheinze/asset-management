package com.accounted4.assetmanager.finance.loan;

import com.accounted4.assetmanager.UiRouter;
import com.accounted4.assetmanager.util.vaadin.ui.DefaultView;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Panel;
import com.vaadin.ui.themes.Reindeer;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;


/**
 *
 * @author gheinze
 */
@UIScope
@SpringView(name = UiRouter.ViewName.PAYMENT_CALCULATOR)
//@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class PaymentScheduleCalculator extends Panel implements DefaultView {

    private static final long serialVersionUID = 1L;

    private final TermsPanel termsPanel;

    @Autowired
    public PaymentScheduleCalculator(LoanService loanService) {
        this.termsPanel = new TermsPanel(loanService);
    }

    @PostConstruct
    private void init() {
        setContent(termsPanel);
        setSizeUndefined();
        addStyleName(Reindeer.PANEL_LIGHT);
    }

}