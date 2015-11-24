/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.accounted4.finance.validation;

import javax.money.Monetary;
import javax.money.MonetaryAmount;
import javax.validation.constraints.DecimalMin;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 *
 * @author gheinze
 */
public class MonetaryAmountDecimalMinValidatorTest {

    public MonetaryAmountDecimalMinValidatorTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @Test
    public void testDecMinEqualExclusive() {

        String minDecAmount = "0.125";
        boolean inclusive = false;

        MonetaryAmountDecimalMinValidator validator = getValidator(minDecAmount, inclusive);
        boolean valid = validator.isValid(ofUSD(0.125), null);

        assertEquals("MinDecimal exclusive", false, valid);

    }

    @Test
    public void testDecMinEqualInclusive() {

        String minDecAmount = "0.125";
        boolean inclusive = true;

        MonetaryAmountDecimalMinValidator validator = getValidator(minDecAmount, inclusive);
        boolean valid = validator.isValid(ofUSD(0.125), null);

        assertEquals("MinDecimal inclusive", true, valid);

    }


    @Test
    public void testDecMinEqualLess() {

        String minDecAmount = "0.125";
        boolean inclusive = true;

        MonetaryAmountDecimalMinValidator validator = getValidator(minDecAmount, inclusive);
        boolean valid = validator.isValid(ofUSD(0.1), null);

        assertEquals("MinDecimal less than", false, valid);

    }


    @Test
    public void testDecMinEqualNull() {

        String minDecAmount = "0.125";
        boolean inclusive = true;

        MonetaryAmountDecimalMinValidator validator = getValidator(minDecAmount, inclusive);
        boolean valid = validator.isValid(null, null);

        assertEquals("MinDecimal inclusive", false, valid);

    }


    private MonetaryAmountDecimalMinValidator getValidator(String threshold, boolean inclusive) {

        DecimalMin annotation = mock(DecimalMin.class);
        when(annotation.value()).thenReturn(threshold);
        when(annotation.inclusive()).thenReturn(inclusive);

        MonetaryAmountDecimalMinValidator validator = new MonetaryAmountDecimalMinValidator();
        validator.initialize(annotation);

        return validator;
    }


    private MonetaryAmount ofUSD(double amount) {
        return Monetary.getDefaultAmountFactory()
                .setCurrency("USD")
                .setNumber(amount)
                .create();

    }

}
