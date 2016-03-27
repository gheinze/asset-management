package com.accounted4.assetmanager.ui.address;

import com.accounted4.assetmanager.entity.Address;
import com.accounted4.assetmanager.entity.Party;
import com.accounted4.assetmanager.repository.PartyRepository;
import com.accounted4.assetmanager.util.vaadin.ui.DefaultView;
import com.accounted4.assetmanager.util.vaadin.ui.FormEditToolBar;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Button;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import org.vaadin.viritin.fields.MTable;
import org.vaadin.viritin.layouts.MVerticalLayout;

/**
 * UI component to display addresses with 3 basic parts:
 *   o toolbar on top for standard crud operations. (FormEditToolBar)
 *   o table of addresses below (MTable)
 *   o popup data entry/edit form invoked by the toolbar (AddressEntryForm)
 * @author gheinze
 */
@UIScope
@SpringView
public class AddressSpringView extends MVerticalLayout implements DefaultView {

    private static final long serialVersionUID = 1L;

    private final AddressEntryFormSpringView addressEntryForm;
    private final PartyRepository partyRepo;

    private final MTable<Address> addressTable = new MTable<>(Address.class)
            .withProperties("address", "note")
            .withColumnHeaders("Address", "Note")
            .setSortableProperties("address")
            .withFullWidth()
            .withFullHeight()
            ;

    private Party selectedParty;

    private final FormEditToolBar editToolBar;


    @Inject
    public AddressSpringView(AddressEntryFormSpringView addressEntryForm, PartyRepository partyRepo) {
        this.addressEntryForm = addressEntryForm;
        this.partyRepo = partyRepo;
        this.editToolBar = new FormEditToolBar(this::add, this::edit, this::remove);
    }


    @PostConstruct
    public void init() {
        addComponent(new MVerticalLayout(editToolBar, addressTable).expand(addressTable));
        addressTable.addMValueChangeListener(e -> adjustActionButtonState());
        setWidth("100%");
        setHeight("100%");
    }

    protected void adjustActionButtonState() {
        boolean hasSelection = addressTable.getValue() != null;
        editToolBar.adjustActionButtonState(hasSelection);
    }


    private void listAddresses() {
        addressTable.setBeans(selectedParty.getAddresses());
        adjustActionButtonState();
    }

    public void add(Button.ClickEvent clickEvent) {
        edit((Address)null);
    }

    public void edit(Button.ClickEvent e) {
        edit(addressTable.getValue());
    }

    public void remove(Button.ClickEvent e) {
        selectedParty.getAddresses().remove(addressTable.getValue());
        persistParty();
        // TODO: got rid of mapping, what about getting rid of address?
    }

    protected void edit(final Address address) {
        addressEntryForm.setAddress(address);
        addressEntryForm.openInModalPopup();
        addressEntryForm.setSavedHandler(this::saveEntry);
        addressEntryForm.setResetHandler(this::resetEntry);
    }

    public void saveEntry(Address address) {
        selectedParty.getAddresses().add(address);
        persistParty();
        closeWindow();
    }

    private void persistParty() {
        partyRepo.save(selectedParty);
        selectedParty = partyRepo.findOne(selectedParty.getId());
        listAddresses();
    }

    public void resetEntry(Address address) {
        listAddresses();
        closeWindow();
    }

    protected void closeWindow() {
        getUI().getWindows().stream().forEach(w -> getUI().removeWindow(w));
    }

    public void setParty(Party selectedParty) {
        this.selectedParty = selectedParty;
        listAddresses();
    }


}
