package com.accounted4.assetmanager.util.vaadin.converter;

import com.accounted4.assetmanager.VaadinUI;
import com.accounted4.finance.math.HalfUpMonetaryRounder;
import com.vaadin.data.util.converter.Converter;
import com.vaadin.server.VaadinSession;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;
import javax.money.MonetaryAmount;
import javax.money.MonetaryOperator;
import org.javamoney.moneta.Money;

/**
 *
 * @author gheinze
 */
public class MonetaryAmountConverter implements Converter<String, MonetaryAmount> {

    private static final MonetaryOperator HALF_UP_ROUNDING_MODE = new HalfUpMonetaryRounder();

    private final DecimalFormat currencyFormatterWithoutSymbol;


    public MonetaryAmountConverter() {
        currencyFormatterWithoutSymbol = getCurrencyFormatterWithoutCurrencySymbol();
    }

    private DecimalFormat getCurrencyFormatterWithoutCurrencySymbol() {
        Locale locale = (Locale)VaadinSession.getCurrent().getAttribute(VaadinUI.LOCALE_KEY);
        DecimalFormat formatter = (DecimalFormat) NumberFormat.getCurrencyInstance(locale);
        DecimalFormatSymbols symbols = formatter.getDecimalFormatSymbols();
        symbols.setCurrencySymbol("");
        formatter.setDecimalFormatSymbols(symbols);
        return formatter;
    }


    @Override
    public Class<MonetaryAmount> getModelType() {
        return MonetaryAmount.class;
    }


    @Override
    public Class<String> getPresentationType() {
        return String.class;
    }


    @Override
    public MonetaryAmount convertToModel(String value, Class<? extends MonetaryAmount> targetType, Locale locale) throws ConversionException {
        if (null == value || value.isEmpty()) {
            return null;
        }
        try {
            // TODO: default locale, currency code, isn't dynamic during the session
            Number number = currencyFormatterWithoutSymbol.parse(value);
            String currencyCode = currencyFormatterWithoutSymbol.getCurrency().getCurrencyCode();
            return Money.of(number, currencyCode);
        } catch (IllegalArgumentException | ParseException e) {
            throw new ConversionException(e);
        }
    }


    @Override
    public String convertToPresentation(MonetaryAmount value, Class<? extends String> targetType, Locale locale) throws ConversionException {
        return null == value ? "" : currencyFormatterWithoutSymbol.format(value.with(HALF_UP_ROUNDING_MODE).getNumber());
    }

}
