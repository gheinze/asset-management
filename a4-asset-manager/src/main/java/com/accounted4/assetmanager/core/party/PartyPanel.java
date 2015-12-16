package com.accounted4.assetmanager.core.party;

import com.accounted4.assetmanager.UiRouter;
import com.accounted4.assetmanager.core.address.Address;
import com.accounted4.assetmanager.core.address.AddressEntryForm;
import com.accounted4.assetmanager.util.vaadin.ui.DefaultView;
import com.vaadin.data.Property;
import com.vaadin.server.FontAwesome;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.RichTextArea;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Reindeer;
import javax.annotation.PostConstruct;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
import org.springframework.beans.factory.annotation.Autowired;
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
@SpringView(name = UiRouter.ViewName.PARTIES)
public class PartyPanel extends Panel implements DefaultView {

    private final PartyRepository partyRepo;
    private final PartyNoteRepository partyNoteRepo;
    private final AddressEntryForm addressEntryForm;

    private final PartySelector partySelector;
    private final VerticalLayout partyDetailContainer;

    private MTable<Address> addressList = new MTable<>(Address.class)
            .withProperties("address", "note")
            .withColumnHeaders("Address", "Note")
            .setSortableProperties("address")
            .withFullWidth();

    private final Button addNew = new MButton(FontAwesome.PLUS, this::add);
    private final Button edit = new MButton(FontAwesome.PENCIL_SQUARE_O, this::edit);
    private final Button delete = new ConfirmButton(FontAwesome.TRASH_O,
            "Are you sure you want to delete the entry?", this::remove);

    @Autowired
    public PartyPanel(PartyRepository repo, PartyNoteRepository partyNoteRepo, AddressEntryForm addressEntryForm) {
        super("Parties");
        this.partyRepo = repo;
        this.partyNoteRepo = partyNoteRepo;
        this.addressEntryForm = addressEntryForm;
        partySelector = new PartySelector(repo);
        partyDetailContainer = new VerticalLayout();
    }

    @PostConstruct
    public void init() {

        addNew.addStyleName("greenicon");
        delete.addStyleName("redicon");

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
        String newParty = String.valueOf(event.getProperty().getValue());
        setupPartyTabs(newParty);
    }

    private void setupPartyTabs(String party) {

        TabSheet partyTabSheet = new TabSheet();

        partyTabSheet.setWidth("100%");
        partyTabSheet.setHeight("100%");

        partyTabSheet.addTab(getNotesArea(), "Notes");
        partyTabSheet.addTab(getAddressArea(), "Addresses");

        partyDetailContainer.removeAllComponents();
        partyDetailContainer.addComponent(partyTabSheet);

    }


    private RichTextArea getNotesArea() {

        RichTextArea noteArea = new RichTextArea();
        noteArea.addStyleName("noImageButton");
        noteArea.setWidth("100%");
        noteArea.setHeight("100%");

        String richText = getPartyNote().getNote();
        noteArea.setValue(null == richText ? "" : richText);

        noteArea.addValueChangeListener(event -> {
            PartyNote partyNote = getPartyNote();
            partyNote.setNote(Jsoup.clean(noteArea.getValue(), Whitelist.simpleText()));
            partyNoteRepo.save(partyNote);
        });

        return noteArea;
    }

    private PartyNote getPartyNote() {
        Party selectedParty = partySelector.getSelectedParty();
        PartyNote partyNote = selectedParty.getNote();
        if (null == partyNote) {
            partyNote = new PartyNote();
            partyNote.setParty(selectedParty);
            selectedParty.setNote(partyNote);
        }
        return partyNote;
    }

    private Component getAddressArea() {
        MVerticalLayout layout = new MVerticalLayout(
                        new MHorizontalLayout(addNew, edit, delete),
                        addressList
                ).expand(addressList);
        listAddresses();
        addressList.addMValueChangeListener(e -> adjustActionButtonState());
        return layout;
    }

    protected void adjustActionButtonState() {
        boolean hasSelection = addressList.getValue() != null;
        edit.setEnabled(hasSelection);
        delete.setEnabled(hasSelection);
    }


    private void listAddresses() {
        Party selectedParty = partySelector.getSelectedParty();
        addressList.setBeans(selectedParty.getAddresses());
        adjustActionButtonState();
    }

    public void add(Button.ClickEvent clickEvent) {
        Address address = null;
        edit(address);
    }

    public void edit(Button.ClickEvent e) {
        edit(addressList.getValue());
    }

    public void remove(Button.ClickEvent e) {
        Party selectedParty = partySelector.getSelectedParty();
        selectedParty.getAddresses().remove(addressList.getValue());
        addressList.setValue(null);
        listAddresses();
    }

    protected void edit(final Address address) {
        addressEntryForm.setAddress(address);
        addressEntryForm.openInModalPopup();
        addressEntryForm.setSavedHandler(this::saveEntry);
        addressEntryForm.setResetHandler(this::resetEntry);
    }

    public void saveEntry(Address address) {
        Party selectedParty = partySelector.getSelectedParty();
        selectedParty.getAddresses().add(address);
        listAddresses();
        closeWindow();
    }

    public void resetEntry(Address address) {
        listAddresses();
        closeWindow();
    }

    protected void closeWindow() {
        getUI().getWindows().stream().forEach(w -> getUI().removeWindow(w));
    }
}
