package com.accounted4.assetmanager.util.vaadin.ui;

import com.vaadin.data.Property;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import org.vaadin.viritin.fields.MTable;

/**
 *
 * @author gheinze
 * @param <T>
 */
public class AmMTable<T> extends MTable<T> {

    public AmMTable(Class<T> type) {
        super(type);
    }


   private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MMM-dd");


    @Override
    protected String formatPropertyValue(Object rowId, Object colId, Property property) {

        Object value = property.getValue();

        if (value instanceof LocalDate) {
           return ((LocalDate)value).format(formatter);
        }

        return super.formatPropertyValue(rowId, colId, property);
    }


}
