package com.accounted4.assetmanager.util.vaadin.ui;

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
import java.util.function.Consumer;
import java.util.function.Function;
import org.vaadin.dialogs.ConfirmDialog;

/**
 * UI component for the selection and creation of new items, allowing for "inactive" filtering.
 *
 * @author gheinze
 * @param <R> Class of the selected type.
 */
public class Selector<R> extends VerticalLayout {

    private static final long serialVersionUID = 1L;


    private final Function<Boolean, BeanContainer<String, R>> beanContainerGenerator;
    private final Consumer<String> newItemPersistor;

    private final ComboBox selector;
    private final CheckBox showInactiveCheckBox;

    private final List<Property.ValueChangeListener> valueChangeListeners;

    private boolean enableValueChangeEventFiring = true;
    private BeanContainer<String, R> beanContainer;


    /**
     *
     * @param beanContainerGenerator A method to create the backing bean container for the drop down combobox.
     * The combobox has a String for display purposes and the backing bean type, R.
     * @param newItemPersistor Method to create a new R object based on the String entered into the combobox.
     */
    public Selector(Function<Boolean, BeanContainer<String, R>> beanContainerGenerator, Consumer<String> newItemPersistor) {
        this.beanContainerGenerator = beanContainerGenerator;
        this.newItemPersistor = newItemPersistor;
        valueChangeListeners = new ArrayList<>();
        this.selector = new ComboBox();
        this.showInactiveCheckBox = new CheckBox("show inactive");
        init();
    }

    public void addValueChangeListener(Property.ValueChangeListener listener) {
        valueChangeListeners.add(listener);
    }


    public String getValue() {
        return (String)selector.getValue();
    }

    public R getSelected() {
        return beanContainer.getItem(getValue()).getBean();
    }

    private void init() {
        setupSelector();
        showInactiveCheckBox.setImmediate(true);
        addComponents(selector, showInactiveCheckBox);
        selector.addValueChangeListener(e -> {
            fireLocalValueChangeListenersIfNecessary(e);
        });
    }


    private void setupSelector() {

        selector.setInputPrompt("Filter");
        selector.setDescription("Type a new name and hit Enter to create a new list item");
        selector.setFilteringMode(FilteringMode.CONTAINS);
        selector.setNullSelectionAllowed(false);
        selector.setImmediate(true);

        selector.addFocusListener(e -> refreshCombobox());

        selector.setNewItemsAllowed(true);

        selector.setNewItemHandler(newItemCaption -> {
            ConfirmDialog.show(getUI(), "Create new item: " + newItemCaption + "?", dialog -> {
                if (dialog.isConfirmed()) {
                    persistNewItem(newItemCaption);
                    refreshCombobox();
                    selector.setValue(newItemCaption);
                }
            });
        });

    }


    private void refreshCombobox() {

        // Don't fire change events due to combo box refreshes from the datastore
        enableValueChangeEventFiring = false;

        String selected = (String)selector.getValue();

        beanContainer = beanContainerGenerator.apply(showInactiveCheckBox.getValue());

        selector.setContainerDataSource(beanContainer);
        selector.setValue(selected);

        enableValueChangeEventFiring = true;

    }


    private void persistNewItem(String newItemName) {
        newItemPersistor.accept(newItemName);
        new Notification(newItemName + " has been created.", "", Notification.Type.TRAY_NOTIFICATION, true).show(Page.getCurrent());
    }


    private void fireLocalValueChangeListenersIfNecessary(ValueChangeEvent e) {
        if (enableValueChangeEventFiring) {
            valueChangeListeners.stream().forEach(listener -> {
                listener.valueChange(e);
            });
        }
    }

}
