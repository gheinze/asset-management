package com.accounted4.assetmanager.core.party;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.server.Page;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Notification;
import com.vaadin.ui.VerticalLayout;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.vaadin.dialogs.ConfirmDialog;

/**
 * UI component for the selection and creation of new parties.
 *
 * @author gheinze
 */
public class PartySelector extends VerticalLayout {

    private static final long serialVersionUID = 1L;

    private final PartyRepository repo;

    private final ComboBox partySelector;
    private final CheckBox showInactiveCheckBox;

    private final List<Property.ValueChangeListener> valueChangeListeners;

    private boolean enableValueChangeEventFiring = true;
    private BeanContainer<String, Party> beanContainer;


    public PartySelector(PartyRepository repo) {
        valueChangeListeners = new ArrayList<>();
        this.repo = repo;
        this.partySelector = new ComboBox();
        this.showInactiveCheckBox = new CheckBox("show inactive Parties");
        init();
    }

    public void addValueChangeListener(Property.ValueChangeListener listener) {
        valueChangeListeners.add(listener);
    }


    public String getValue() {
        return (String)partySelector.getValue();
    }

    public Party getSelectedParty() {
        return beanContainer.getItem(getValue()).getBean();
    }

    private void init() {
        setupPartySelector();
        showInactiveCheckBox.setImmediate(true);
        addComponents(partySelector, showInactiveCheckBox);
        partySelector.addValueChangeListener(e -> {
            fireLocalValueChangeListenersIfNecessary(e);
        });
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

        // Don't fire change events due to combo box refreshes from the datastore
        enableValueChangeEventFiring = false;

        String selectedParty = (String)partySelector.getValue();

        beanContainer = new BeanContainer<>(Party.class);
        beanContainer.setBeanIdProperty("partyName");
        beanContainer.addAll(
                showInactiveCheckBox.getValue() ?
                repo.findAll(new Sort("partyName")) :
                repo.findByInactiveOrderByPartyName(false)
        );

        partySelector.setContainerDataSource(beanContainer);
        partySelector.setValue(selectedParty);

        enableValueChangeEventFiring = true;

    }


    private void persistNewParty(String partyName) {
        Party newParty = new Party();
        newParty.setPartyName(partyName);
        newParty.setInactive(false);
        repo.save(newParty);
        new Notification(partyName + " has been created.", "", Notification.Type.TRAY_NOTIFICATION, true).show(Page.getCurrent());
    }


    private void fireLocalValueChangeListenersIfNecessary(ValueChangeEvent e) {
        if (enableValueChangeEventFiring) {
            valueChangeListeners.stream().forEach(listener -> {
                listener.valueChange(e);
            });
        }
    }

}
