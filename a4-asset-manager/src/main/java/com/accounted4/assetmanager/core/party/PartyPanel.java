package com.accounted4.assetmanager.core.party;

import com.accounted4.assetmanager.UiRouter;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

/**
 *
 * @author gheinze
 */
@UIScope
@SpringView(name = UiRouter.ViewName.PARTIES)
public class PartyPanel extends Panel implements View {

    private final PartyRepository repo;

    private final Grid grid;
    private final TextField filter;
    private final Button addNewBtn;

    @Autowired
    public PartyPanel(PartyRepository repo) {
        super("Parties");
        this.repo = repo;
        this.grid = new Grid();
        this.filter = new TextField();
        this.addNewBtn = new Button("New party", FontAwesome.PLUS);
    }

    @PostConstruct
    public void init() {
        filter.setInputPrompt("Filter by name");
        filter.addTextChangeListener(e -> listParties(e.getText()));
        VerticalLayout mainLayout = new VerticalLayout(filter, grid);
        setContent(mainLayout);
    }


    private void listParties(String text) {

        grid.setContainerDataSource(
                new BeanItemContainer<>(Party.class, StringUtils.isEmpty(text) ?
                        repo.findAll() :
                        repo.findByPartyNameStartsWithIgnoreCase(text)
        ));

    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
    }

}
