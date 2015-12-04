package com.accounted4.assetmanager.core.party;

import com.accounted4.assetmanager.UiRouter;
import com.accounted4.assetmanager.util.vaadin.ui.DefaultView;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.server.Page;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Reindeer;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.vaadin.dialogs.ConfirmDialog;

/**
 *
 * @author gheinze
 */
@UIScope
@SpringView(name = UiRouter.ViewName.PARTIES)
public class PartyPanel extends Panel implements DefaultView {

    private final PartyRepository repo;

    private final ComboBox partySelector;
    private final CheckBox showInactiveCheckBox;

    @Autowired
    public PartyPanel(PartyRepository repo) {
        super("Parties");
        this.repo = repo;
        this.partySelector = new ComboBox();
        this.showInactiveCheckBox = new CheckBox("show inactive Parties");
    }

    @PostConstruct
    public void init() {

        setupPartySelector();

        showInactiveCheckBox.setImmediate(true);

        VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.addComponents(partySelector, showInactiveCheckBox);

        setContent(mainLayout);

        setSizeUndefined();
        addStyleName(Reindeer.PANEL_LIGHT);

    }


    private void setupPartySelector() {

        partySelector.setInputPrompt("Filter by name");
        partySelector.setDescription("Type a new name and hit Enter to create a new Party");
        partySelector.setFilteringMode(FilteringMode.CONTAINS);
        partySelector.setItemCaptionPropertyId("partyName");
        partySelector.setNullSelectionAllowed(false);
        partySelector.setImmediate(true);
        partySelector.addFocusListener(e -> refreshPartyCombobox());

        partySelector.setNewItemsAllowed(true);

        partySelector.setNewItemHandler(newItemCaption -> {
            ConfirmDialog.show(getUI(), "Create new Party: " + newItemCaption + "?", dialog -> {
                if (dialog.isConfirmed()) {
                    persistNewParty(newItemCaption);
                    refreshPartyCombobox();
                    partySelector.setValue(newItemCaption);
                }
            });
        });

    }


    private void refreshPartyCombobox() {

        Object selectedParty = partySelector.getValue();

        BeanContainer<String, Party> beanContainer = new BeanContainer<>(Party.class);
        beanContainer.setBeanIdProperty("partyName");
        beanContainer.addAll(
                showInactiveCheckBox.getValue() ?
                repo.findAll(new Sort("partyName")) :
                repo.findByInactiveOrderByPartyName(false)
        );

        partySelector.setContainerDataSource(beanContainer);
        partySelector.setValue(selectedParty);

    }


    private void persistNewParty(String partyName) {
        Party newParty = new Party();
        newParty.setPartyName(partyName);
        newParty.setInactive(false);
        repo.save(newParty);
        new Notification(partyName + " has been created.", "", Notification.Type.TRAY_NOTIFICATION, true).show(Page.getCurrent());
    }

}
