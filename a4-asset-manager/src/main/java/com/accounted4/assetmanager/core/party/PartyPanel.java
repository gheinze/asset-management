package com.accounted4.assetmanager.core.party;

import com.accounted4.assetmanager.UiRouter;
import com.accounted4.assetmanager.util.vaadin.ui.DefaultView;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.RichTextArea;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Reindeer;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author gheinze
 */
@UIScope
@SpringView(name = UiRouter.ViewName.PARTIES)
public class PartyPanel extends Panel implements DefaultView {

    private final PartyRepository repo;

    private final PartySelector partySelector;
    private final TabSheet partyTabSheet;

    @Autowired
    public PartyPanel(PartyRepository repo) {
        super("Parties");
        this.repo = repo;
        partySelector = new PartySelector(repo);
        partyTabSheet = new TabSheet();
    }

    @PostConstruct
    public void init() {

        setupPartyTabs();

        Label verticalSpacer = new Label();
        verticalSpacer.setHeight("10px");
        verticalSpacer.setWidth("100%");

        VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.addComponents(partySelector, verticalSpacer, partyTabSheet);
        mainLayout.setSizeFull();
        mainLayout.setExpandRatio(partyTabSheet, 1.0f);

        setContent(mainLayout);

        setSizeFull();
        addStyleName(Reindeer.PANEL_LIGHT);

    }

    private void setupPartyTabs() {

        RichTextArea notes = new RichTextArea();
        notes.setWidth("100%");
        notes.setHeight("100%");

        partyTabSheet.setWidth("100%");
        partyTabSheet.setHeight("100%");
        partyTabSheet.addTab(notes, "Notes");

    }

}
