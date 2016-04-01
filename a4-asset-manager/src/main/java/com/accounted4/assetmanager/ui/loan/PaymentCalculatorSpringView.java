package com.accounted4.assetmanager.ui.loan;

import com.accounted4.assetmanager.service.LoanService;
import com.accounted4.assetmanager.UiRouter;
import com.accounted4.assetmanager.util.vaadin.ui.DefaultView;
import com.accounted4.assetmanager.util.vaadin.ui.HelpSlider;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Panel;
import com.vaadin.ui.themes.Reindeer;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.sliderpanel.SliderPanel;
import org.vaadin.viritin.layouts.MHorizontalLayout;


/**
 *
 * @author gheinze
 */
@UIScope
@SpringView(name = UiRouter.ViewName.PAYMENT_CALCULATOR)
//@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class PaymentCalculatorSpringView extends Panel implements DefaultView {

    private static final long serialVersionUID = 1L;

    private final LoanTermsPanel termsPanel;

    @Autowired
    public PaymentCalculatorSpringView(LoanService loanService) {
        this.termsPanel = new LoanTermsPanel(loanService);
    }

    @PostConstruct
    private void init() {

        SliderPanel rightSlider = HelpSlider.create("PaymentCalculator.html");

        final MHorizontalLayout mainLayout = new MHorizontalLayout();
        mainLayout.setSpacing(false);
        mainLayout.setSizeFull();
        mainLayout.addComponents(termsPanel, rightSlider);
        mainLayout.setExpandRatio(termsPanel, 1.0f);

        setContent(mainLayout);
        setSizeFull();
        addStyleName(Reindeer.PANEL_LIGHT);
    }

}