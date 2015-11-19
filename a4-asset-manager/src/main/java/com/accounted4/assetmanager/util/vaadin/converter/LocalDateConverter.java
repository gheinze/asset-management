package com.accounted4.assetmanager.util.vaadin.converter;

import com.vaadin.data.util.converter.Converter;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Locale;

/**
 * Convert "java.util.Date" models used by DateField components to and from java.time.LocalDate objects.
 *
 * @author gheinze
 */
public class LocalDateConverter implements Converter<Date, LocalDate> {

    @Override
    public LocalDate convertToModel(Date value, Class<? extends LocalDate> targetType, Locale locale) throws ConversionException {
        return convertDateToLocalDate(value);
    }

    @Override
    public Date convertToPresentation(LocalDate value, Class<? extends Date> targetType, Locale locale) throws ConversionException {
        return convertLocalDateToDate(value);
    }

    @Override
    public Class<LocalDate> getModelType() {
        return LocalDate.class;
    }

    @Override
    public Class<Date> getPresentationType() {
        return Date.class;
    }


    // See: http://stackoverflow.com/questions/21242110/convert-java-util-date-to-java-time-localdate

    public static LocalDate convertDateToLocalDate(Date date) {
        return null == date ? null : date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    public static Date convertLocalDateToDate(LocalDate localDate) {
        return null == localDate ? null : Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

}
