package com.accounted4.assetmanager.util.vaadin.ui;

import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import org.vaadin.viritin.button.ConfirmButton;
import org.vaadin.viritin.button.MButton;
import org.vaadin.viritin.layouts.MHorizontalLayout;

/**
 * A Tool Bar component with add/edit/delete buttons.
 *
 * @author gheinze
 */
public class FormEditToolBar extends MHorizontalLayout {

    private final Button addNew;
    private final Button edit;
    private final Button delete;

    public FormEditToolBar(ClickListener addListener, ClickListener editListener, ClickListener deleteListener) {

        this.addNew = new MButton(FontAwesome.PLUS, addListener);
        addNew.addStyleName("greenicon");

        this.edit = new MButton(FontAwesome.PENCIL_SQUARE_O, editListener);

        this.delete = new ConfirmButton(FontAwesome.TRASH_O, "Are you sure you want to delete the entry?", deleteListener);
        delete.addStyleName("redicon");

        add(addNew, edit, delete);

    }

    public void adjustActionButtonState(boolean hasSelection) {
        edit.setEnabled(hasSelection);
        delete.setEnabled(hasSelection);
    }

}
