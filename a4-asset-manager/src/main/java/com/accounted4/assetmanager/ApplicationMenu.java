package com.accounted4.assetmanager;

import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Tree;
import javax.annotation.PostConstruct;

/**
 *
 * @author gheinze
 */
@UIScope
@SpringComponent
public class ApplicationMenu extends Tree {

    @PostConstruct
    public void init() {
        populateMenuFromUiRouter();
        configureValueChangeListener();
    }

    private void populateMenuFromUiRouter() {
        for (UiRouter menuItem : UiRouter.values()) {
            addItem(menuItem.getDisplayName());
            if (null != menuItem.getParent()) {
                setParent(menuItem.getDisplayName(), menuItem.getParent().getDisplayName());
                setChildrenAllowed(menuItem.getDisplayName(), false);  // Assumes one level of nesting for now
            } else {
                expandItem(menuItem.getDisplayName());
            }
        }
    }

    private void configureValueChangeListener() {
        addValueChangeListener(e -> {
            String selectedItemDisplayName = String.valueOf(e.getProperty().getValue());
            for (UiRouter menuItem : UiRouter.values()) {
                if (menuItem.isNavigable() && menuItem.getDisplayName().equals(selectedItemDisplayName)) {
                    getUI().getNavigator().navigateTo(menuItem.getViewName());
                    return;
                }
            }
        });
    }

}
