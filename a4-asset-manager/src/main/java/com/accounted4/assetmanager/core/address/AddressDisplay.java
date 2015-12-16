package com.accounted4.assetmanager.core.address;

import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Button;
import org.vaadin.viritin.button.ConfirmButton;
import org.vaadin.viritin.button.MButton;
import org.vaadin.viritin.fields.MTable;

/**
 *
 * @author gheinze
 */
public class AddressDisplay {

    private MTable<Address> addressList = new MTable<>(Address.class)
            .withProperties("address", "note")
            .withColumnHeaders("Address", "Note")
            .setSortableProperties("address")
            .withFullWidth();

//    private final Button addNew = new MButton(FontAwesome.PLUS, this::add);
//    private final Button edit = new MButton(FontAwesome.PENCIL_SQUARE_O, this::edit);
//    private final Button delete = new ConfirmButton(FontAwesome.TRASH_O,
//            "Are you sure you want to delete the entry?", this::remove);


}
