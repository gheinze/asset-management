package com.accounted4.assetmanager.core.party;

import com.accounted4.assetmanager.UiRouter;
import com.accounted4.assetmanager.core.address.AddressDisplay;
import com.accounted4.assetmanager.util.vaadin.ui.DefaultView;
import com.accounted4.assetmanager.util.vaadin.ui.Selector;
import com.vaadin.data.Property;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.server.Page;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.RichTextArea;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Reindeer;
import java.util.function.Consumer;
import java.util.function.Function;
import javax.annotation.PostConstruct;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;

/**
 *
 * @author gheinze
 */
@UIScope
@SpringView(name = UiRouter.ViewName.PARTIES)
public class PartyPanel extends Panel implements DefaultView {

    private final PartyRepository partyRepo;
    private final PartyNoteRepository partyNoteRepo;
    private final AddressDisplay addressDisplay;

    private final Selector<Party> partySelector;
    private final VerticalLayout partyDetailContainer;


    @Autowired
    public PartyPanel(PartyRepository partyRepo, PartyNoteRepository partyNoteRepo, AddressDisplay addressDisplay) {
        super("Parties");
        this.partyRepo = partyRepo;
        this.partyNoteRepo = partyNoteRepo;
        this.addressDisplay = addressDisplay;
        partySelector = createPartySelector();
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


    private static final String PARTY_NAME_FIELD = "partyName";

    private Selector<Party> createPartySelector() {

        Function<Boolean, BeanContainer<String, Party>> beanContainerGenerator = (showInactive) -> {
            BeanContainer<String, Party> beanContainer = new BeanContainer<>(Party.class);
            beanContainer.setBeanIdProperty(PARTY_NAME_FIELD);
            beanContainer.addAll(
                    showInactive
                            ? partyRepo.findAll(new Sort(PARTY_NAME_FIELD))
                            : partyRepo.findByInactiveOrderByPartyName(false)
            );
            return beanContainer;
        };


        Consumer<String> newItemPersistor = (partyName) -> {
            Party newParty = new Party();
            newParty.setPartyName(partyName);
            newParty.setInactive(false);
            partyRepo.save(newParty);
            new Notification(partyName + " has been created.", "", Notification.Type.TRAY_NOTIFICATION, true).show(Page.getCurrent());
        };


        Selector<Party> selector = new Selector<>(beanContainerGenerator, newItemPersistor);

        return selector;
    }

    private void selectedPartyChanged(Property.ValueChangeEvent event) {
        setupPartyTabs();
    }

    private void setupPartyTabs() {

        TabSheet partyTabSheet = new TabSheet();

        partyTabSheet.setWidth("100%");
        partyTabSheet.setHeight("100%");

        addressDisplay.setParty(partySelector.getSelected());

        partyTabSheet.addTab(getNotesArea(), "Notes");
        partyTabSheet.addTab(addressDisplay, "Addresses");

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
        Party selectedParty = partySelector.getSelected();
        PartyNote partyNote = selectedParty.getNote();
        if (null == partyNote) {
            partyNote = new PartyNote();
            partyNote.setParty(selectedParty);
            selectedParty.setNote(partyNote);
        }
        return partyNote;
    }

}
