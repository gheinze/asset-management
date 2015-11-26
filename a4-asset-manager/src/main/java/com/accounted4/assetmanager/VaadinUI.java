package com.accounted4.assetmanager;

//import com.accounted4.assetmanager.core.party.PartyRepository;
import com.accounted4.assetmanager.finance.loan.LoanService;
import com.accounted4.assetmanager.finance.loan.PaymentScheduleCalculator;
import com.accounted4.assetmanager.util.vaadin.converter.ConverterFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.annotations.Theme;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinSession;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import java.util.Locale;

@SpringUI
@Theme("reindeer")
public class VaadinUI extends UI {

    @Autowired LoanService loanService;

    public static final String LOCALE_KEY = "LOCALE";

    //private final PartyRepository repo;

    private final Grid grid;
    private final TextField filter;
    private final Button addNewBtn;

    //@Autowired
    public VaadinUI() { //PartyRepository repo) {
        //this.repo = repo;
        this.grid = new Grid();
        this.filter = new TextField();
        this.addNewBtn = new Button("New customer", FontAwesome.PLUS);
        VaadinSession.getCurrent().setConverterFactory(new ConverterFactory());
        VaadinSession.getCurrent().setAttribute(LOCALE_KEY, Locale.CANADA);
    }

    @Override
    protected void init(VaadinRequest request) {
        Panel paymentCalculatorPanel = new PaymentScheduleCalculator(loanService);
        paymentCalculatorPanel.setSizeUndefined();
        setContent(paymentCalculatorPanel);
        setSizeUndefined();
    }

    // JPA example will be re-inserted later
    
    // tag::listCustomers[]
//    private void listCustomers(String text) {
//        if (StringUtils.isEmpty(text)) {
//            grid.setContainerDataSource(
//                    new BeanItemContainer(Party.class, repo.findAll()));
//        } else {
//            grid.setContainerDataSource(new BeanItemContainer(Party.class,
//                    repo.findByPartyNameStartsWithIgnoreCase(text)));
//        }
//    }
    // end::listCustomers[]

}
