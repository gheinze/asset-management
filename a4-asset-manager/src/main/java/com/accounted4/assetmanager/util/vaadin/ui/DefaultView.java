package com.accounted4.assetmanager.util.vaadin.ui;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;

/**
 *
 * @author gheinze
 */
public interface DefaultView extends View {

    @Override
    default void enter(ViewChangeListener.ViewChangeEvent event) {
    }

}
