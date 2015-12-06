package com.accounted4.assetmanager.core.party;

import com.accounted4.assetmanager.UiRouter;
import com.accounted4.assetmanager.util.vaadin.ui.DefaultView;
import com.vaadin.data.Property;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
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
    private final VerticalLayout partyDetailContainer;


    @Autowired
    public PartyPanel(PartyRepository repo) {
        super("Parties");
        this.repo = repo;
        partySelector = new PartySelector(repo);
        partyDetailContainer = new VerticalLayout();
    }

    @PostConstruct
    public void init() {

        partySelector.addValueChangeListener(event -> {
            selectedPartyChanged(event);
        });

        partyDetailContainer.setSizeFull();

        Label verticalSpacer = new Label();
        verticalSpacer.setHeight("20px");
        verticalSpacer.setWidth("100%");

        VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.addComponents(partySelector, verticalSpacer, partyDetailContainer);
        mainLayout.setSizeFull();
        mainLayout.setExpandRatio(partyDetailContainer, 1.0f);

        setContent(mainLayout);

        setSizeFull();
        addStyleName(Reindeer.PANEL_LIGHT);

    }

    private void selectedPartyChanged(Property.ValueChangeEvent event) {

        String newParty = (String)event.getProperty().getValue();
        setupPartyTabs(newParty);
        Notification.show(
                "Value changed:",
                String.valueOf(event.getProperty().getValue()),
                Type.TRAY_NOTIFICATION
        );
    }

    private void setupPartyTabs(String party) {

        TabSheet partyTabSheet = new TabSheet();

        partyTabSheet.setWidth("100%");
        partyTabSheet.setHeight("100%");

        partyTabSheet.addTab(getNotesArea(), "Notes for " + party);
        partyTabSheet.addTab(new Label("Tab 2 Content"), "Tab2");

        partyDetailContainer.removeAllComponents();
        partyDetailContainer.addComponent(partyTabSheet);

    }


    private RichTextArea getNotesArea() {
        RichTextArea notes = new RichTextArea();
        notes.setWidth("100%");
        notes.setHeight("100%");
        return notes;
    }

}
