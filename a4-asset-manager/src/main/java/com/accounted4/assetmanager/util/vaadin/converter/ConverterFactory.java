package com.accounted4.assetmanager.util.vaadin.converter;

import com.vaadin.data.util.converter.Converter;
import com.vaadin.data.util.converter.DefaultConverterFactory;
import java.time.LocalDate;
import java.util.Date;
import javax.money.MonetaryAmount;

/**
 * Extend default ConverterFactory with additional converters used for mapping UI components to data models:
 *   o data model: MonetaryAmount presentation: String
 *   o data model: LocalDate presentation: Date
 *
 * To enable automatic discovery of these converters, replace default converter factory with this one:
 *     VaadinSession.getCurrent().setConverterFactory(new ConverterFactory());
 *
 * @author gheinze
 */
public class ConverterFactory extends DefaultConverterFactory {

    private static final long serialVersionUID = 1L;

    @Override
    protected Converter<String, ?> createStringConverter(Class<?> sourceType) {
        if (MonetaryAmount.class.isAssignableFrom(sourceType)) {
            return new MonetaryAmountConverter();
        }
        return super.createStringConverter(sourceType);
    }

    @Override
    protected Converter<Date, ?> createDateConverter(Class<?> sourceType) {
        if (LocalDate.class.isAssignableFrom(sourceType)) {
            return new LocalDateConverter();
        }
        return super.createDateConverter(sourceType);
    }

}