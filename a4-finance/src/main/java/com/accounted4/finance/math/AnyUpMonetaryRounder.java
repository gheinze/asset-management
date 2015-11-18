package com.accounted4.finance.math;

import java.math.BigDecimal;
import java.math.RoundingMode;
import javax.money.Monetary;
import javax.money.MonetaryAmount;
import javax.money.MonetaryOperator;

/**
 *
 * @author gheinze
 */
public class AnyUpMonetaryRounder implements MonetaryOperator {

    @Override
    public MonetaryAmount apply(MonetaryAmount amount) {
        int fractionDigits = amount.getCurrency().getDefaultFractionDigits();
        BigDecimal roundedAmount = amount.getNumber().numberValue(BigDecimal.class)
                .setScale(fractionDigits + 1, RoundingMode.HALF_UP)  // dismiss fractions < .05 of a cent
                .setScale(fractionDigits, RoundingMode.CEILING);     // all remaining fractional cents forced up
        return Monetary.getDefaultAmountFactory()
                .setCurrency(amount.getCurrency())
                .setNumber(roundedAmount)
                .create();
    }

}
