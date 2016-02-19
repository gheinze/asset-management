package com.accounted4.assetmanager.util.vaadin.ui;

import com.vaadin.ui.PopupDateField;
import java.time.LocalDate;

/**
 * Popupdate field configuration commonly used throughout this app.
 * @author gheinze
 */
public class AmPopupDateField extends PopupDateField {

    public AmPopupDateField(String caption) {
        super("Post date");
        setConverter(LocalDate.class);
        setDateFormat("dd-MMM-yyyy");
        setImmediate(true);
        setWidth("10em");
    }

}
