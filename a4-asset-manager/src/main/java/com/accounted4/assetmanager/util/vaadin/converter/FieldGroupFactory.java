package com.accounted4.assetmanager.util.vaadin.converter;

import com.vaadin.data.fieldgroup.DefaultFieldGroupFieldFactory;
import com.vaadin.data.fieldgroup.FieldGroupFieldFactory;
import com.vaadin.ui.Field;
import com.vaadin.ui.PopupDateField;
import java.time.LocalDate;

/**
 * Enhance FieldGroup field creation based on a fields data type. For example, if the field
 * type is LocalDate, create a ui field component of PopupDateField by default.
 *
 * @author gheinze
 */
public class FieldGroupFactory implements FieldGroupFieldFactory {

    private static final long serialVersionUID = 1L;


    @Override
    public <T extends Field> T createField(Class<?> dataType, Class<T> fieldType) {

        if (LocalDate.class.isAssignableFrom(dataType)) {
            return fieldType.cast(createPopupDateField());
        }

        return DefaultFieldGroupFieldFactory.get().createField(dataType, fieldType);
    }


    private PopupDateField createPopupDateField() {
        PopupDateField field = new PopupDateField();
        field.setImmediate(true);
        return field;
    }

}
