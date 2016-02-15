package com.accounted4.assetmanager.util.vaadin.ui;

import com.vaadin.data.Property;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TabSheet.SelectedTabChangeEvent;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Reindeer;
import java.util.ArrayList;
import java.util.function.Consumer;
import java.util.function.Function;
import javax.annotation.PostConstruct;
import lombok.Getter;

/**
 * A master-detail panel with a "master" selector on the top of the pane and
 * tabbed sheets of details related to the selected master below.
 *
 * @param <T> The type of object contained in the master record selector combobox.
 *
 * @author gheinze
 */
public abstract class SelectorDetailPanel<T> extends Panel implements DefaultView {

    private Selector<T> masterSelector;
    private VerticalLayout detailContainer;

    private final ArrayList<Tab> tabs = new ArrayList<>();


    public SelectorDetailPanel(String panelLabel) {
        super(panelLabel);
    }


    /**
     * The "master" combobox for selecting the master record is populated via this generator.
     *
     * @return
     */
    public abstract Function<Boolean, BeanContainer<String, T>> getBeanContainerGenerator();

    /**
     * If a new item is typed into the combobox for the purposes of creating a new master record,
     * this consumer will be used to persist the new record.
     * @return
     */
    public abstract Consumer<String> getNewItemPersistor();


    @PostConstruct
    private void init() {

        masterSelector = new Selector<>(getBeanContainerGenerator(), getNewItemPersistor());

        masterSelector.addValueChangeListener(event -> {
            masterSelectionChanged(event);
        });

        detailContainer = new VerticalLayout();
        detailContainer.setSizeFull();

        Label verticalSpacer = new Label();
        verticalSpacer.setHeight("20px");
        verticalSpacer.setWidth("100%");

        VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.addComponents(masterSelector, verticalSpacer, detailContainer);
        mainLayout.setSizeFull();
        mainLayout.setExpandRatio(detailContainer, 1.0f);

        setContent(mainLayout);

        setSizeFull();
        addStyleName(Reindeer.PANEL_LIGHT);

    }

    public T getSelected() {
        return masterSelector.getSelected();
    }

    protected void masterSelectionChanged(Property.ValueChangeEvent event) {
        recreateDetailTabs();
    }

    private void recreateDetailTabs() {

        TabSheet tabSheet = new TabSheet();

        tabSheet.setWidth("100%");
        tabSheet.setHeight("100%");

        tabs.stream().forEach((tab) -> {
            tabSheet.addTab(tab.getComponentGenerator().apply(getSelected()), tab.getCaption());
        });

        tabSheet.addSelectedTabChangeListener((SelectedTabChangeEvent event) -> {
            TabSheet tabsheet = event.getTabSheet();
            Component tab = tabsheet.getSelectedTab();
            if (tab instanceof Refreshable) {
                ((Refreshable)tab).refresh();
            }
        });

        detailContainer.removeAllComponents();
        detailContainer.addComponent(tabSheet);

    }


    public void addDetailTab(Function<T, Component> componentGenerator, String caption) {
        tabs.add(new Tab(componentGenerator, caption));
    }


    private class Tab {
        @Getter private final Function<T, Component> componentGenerator;
        @Getter private final String caption;
        public Tab(final Function<T, Component> componentGenerator, final String caption) {
            this.componentGenerator = componentGenerator;
            this.caption = caption;
        }
    }

}
