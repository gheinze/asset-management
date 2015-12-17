package com.accounted4.assetmanager.core.address;

import com.accounted4.assetmanager.core.party.Party;
import com.accounted4.assetmanager.core.party.PartyRepository;
import com.accounted4.assetmanager.util.vaadin.ui.DefaultView;
import com.vaadin.server.FontAwesome;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Button;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import org.vaadin.viritin.button.ConfirmButton;
import org.vaadin.viritin.button.MButton;
import org.vaadin.viritin.fields.MTable;
import org.vaadin.viritin.layouts.MHorizontalLayout;
import org.vaadin.viritin.layouts.MVerticalLayout;

/**
 *
 * @author gheinze
 */
@UIScope
@SpringView
public class AddressDisplay extends MVerticalLayout implements DefaultView {

    private static final long serialVersionUID = 1L;

    private final AddressEntryForm addressEntryForm;
    private final PartyRepository partyRepo;

    private final MTable<Address> addressTable = new MTable<>(Address.class)
            .withProperties("address", "note")
            .withColumnHeaders("Address", "Note")
            .setSortableProperties("address")
            .withFullWidth();

    private Party selectedParty;

    private final Button addNew = new MButton(FontAwesome.PLUS, this::add);
    private final Button edit = new MButton(FontAwesome.PENCIL_SQUARE_O, this::edit);
    private final Button delete = new ConfirmButton(FontAwesome.TRASH_O,
            "Are you sure you want to delete the entry?", this::remove);

    @Inject
    public AddressDisplay(AddressEntryForm addressEntryForm, PartyRepository partyRepo) {
        this.addressEntryForm = addressEntryForm;
        this.partyRepo = partyRepo;
    }


    @PostConstruct
    public void init() {

        addNew.addStyleName("greenicon");
        delete.addStyleName("redicon");

        addComponent(new MVerticalLayout(
                new MHorizontalLayout(addNew, edit, delete),
                addressTable
                ).expand(addressTable)
        );

        addressTable.addMValueChangeListener(e -> adjustActionButtonState());

    }

    protected void adjustActionButtonState() {
        boolean hasSelection = addressTable.getValue() != null;
        edit.setEnabled(hasSelection);
        delete.setEnabled(hasSelection);
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
