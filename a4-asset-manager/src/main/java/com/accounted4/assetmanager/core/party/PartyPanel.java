package com.accounted4.assetmanager.core.party;

import com.accounted4.assetmanager.UiRouter;
import com.accounted4.assetmanager.core.address.AddressDisplay;
import com.accounted4.assetmanager.util.vaadin.ui.SelectorDetailPanel;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Component;
import com.vaadin.ui.RichTextArea;
import java.util.function.Consumer;
import java.util.function.Function;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;

/**
 * Master-detail panel for displaying Parties.
 *
 * @author gheinze
 */
@UIScope
@SpringView(name = UiRouter.ViewName.PARTIES)
public class PartyPanel extends SelectorDetailPanel<Party> {

    private final PartyRepository partyRepo;
    private final PartyNoteRepository partyNoteRepo;
    private final AddressDisplay addressDisplay;


    @Autowired
    public PartyPanel(PartyRepository partyRepo, PartyNoteRepository partyNoteRepo, AddressDisplay addressDisplay) {
        super("Parties");
        this.partyRepo = partyRepo;
        this.partyNoteRepo = partyNoteRepo;
        this.addressDisplay = addressDisplay;
        defineTabs();
    }

    private void defineTabs() {
        addDetailTab(getNotesAreaGenerator(), "Notes");
        addDetailTab(getAddressDisplay(), "Addresses");

    }


    private static final String PARTY_NAME_FIELD = "partyName";

    @Override
    public Function<Boolean, BeanContainer<String, Party>> getBeanContainerGenerator() {

        return (showInactive) -> {
            BeanContainer<String, Party> beanContainer = new BeanContainer<>(Party.class);
            beanContainer.setBeanIdProperty(PARTY_NAME_FIELD);
            beanContainer.addAll(
                    showInactive
                            ? partyRepo.findAll(new Sort(PARTY_NAME_FIELD))
                            : partyRepo.findByInactiveOrderByPartyName(false)
            );
            return beanContainer;
        };

    }


    @Override
    public Consumer<String> getNewItemPersistor() {

        return (partyName) -> {
            Party newParty = new Party();
            newParty.setPartyName(partyName);
            newParty.setInactive(false);
            partyRepo.save(newParty);
        };

    }

    private Function<Party, Component> getAddressDisplay() {
        return (selectedParty) -> {
            addressDisplay.setParty(selectedParty);
            return addressDisplay;
        };
    }

    private Function<Party, Component> getNotesAreaGenerator() {

        return (selectedParty) -> {

            RichTextArea noteArea = new RichTextArea();
            noteArea.addStyleName("noImageButton");
            noteArea.setWidth("100%");
            noteArea.setHeight("100%");

            String richText = getPartyNote(selectedParty).getNote();
            noteArea.setValue(null == richText ? "" : richText);

            noteArea.addValueChangeListener(event -> {
                PartyNote partyNote = getPartyNote(selectedParty);
                partyNote.setNote(noteArea.getValue());
//                partyNote.setNote(Jsoup.clean(noteArea.getValue(), Whitelist.simpleText()));
                partyNoteRepo.save(partyNote);
            });

            return noteArea;
        };
    }

    private PartyNote getPartyNote(Party selectedParty) {
        PartyNote partyNote = selectedParty.getNote();
        if (null == partyNote) {
            partyNote = new PartyNote();
            partyNote.setParty(selectedParty);
            selectedParty.setNote(partyNote);
        }
        return partyNote;
    }

}
