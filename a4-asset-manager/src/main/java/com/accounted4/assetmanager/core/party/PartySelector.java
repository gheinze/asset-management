package com.accounted4.assetmanager.core.party;

import com.vaadin.data.util.BeanContainer;
import com.vaadin.server.Page;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Notification;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Reindeer;
import org.springframework.data.domain.Sort;
import org.vaadin.dialogs.ConfirmDialog;

/**
 *
 * @author gheinze
 */
public class PartySelector extends VerticalLayout {

    private final PartyRepository repo;

    private final ComboBox partySelector;
    private final CheckBox showInactiveCheckBox;

    public PartySelector(PartyRepository repo) {
        this.repo = repo;
        this.partySelector = new ComboBox();
        this.showInactiveCheckBox = new CheckBox("show inactive Parties");
        init();
    }

    private void init() {
        setupPartySelector();
        showInactiveCheckBox.setImmediate(true);
        addComponents(partySelector, showInactiveCheckBox);
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
