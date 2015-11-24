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


    @Test
    public void testAnnotationOnBoundaryWithBoundaryExcluded() {
        boolean inclusive = false;
        testAnnotationOnBoundary(inclusive);
    }

    @Test
    public void testAnnotationOnBoundaryWithBoundaryIncluded() {
        boolean inclusive = true;
        testAnnotationOnBoundary(inclusive);
    }

    private void testAnnotationOnBoundary(boolean inclusive) {
        String minDecAmount = "0.125";
        MonetaryAmountDecimalMinValidator validator = getValidator(minDecAmount, inclusive);
        boolean valid = validator.isValid(ofUSD(0.125), null);
        assertEquals("MinDecimal on boundary with inclusive set to " + inclusive, inclusive, valid);

    }


    @Test
    public void testAnnotationWithValueLessThanBoundaryWithInclusiveBoundary() {
        boolean inclusive = true;
        testAnnotationWithValueLessThanBoundary(inclusive);
    }

    @Test
    public void testAnnotationWithValueLessThanBoundaryWithExclusiveBoundary() {
        boolean inclusive = false;
        testAnnotationWithValueLessThanBoundary(inclusive);
    }

    private void testAnnotationWithValueLessThanBoundary(boolean inclusive) {
        String minDecAmount = "0.125";
        MonetaryAmountDecimalMinValidator validator = getValidator(minDecAmount, inclusive);
        boolean valid = validator.isValid(ofUSD(0.1), null);
        assertEquals("MinDecimal before boundary, inclusive = " + inclusive, false, valid);
    }


    @Test
    public void testAnnotationWithValueGreaterThanBoundaryWithInclusiveBoundary() {
        boolean inclusive = true;
        testAnnotationWithValueGreaterThanBoundary(inclusive);
    }

    @Test
    public void testAnnotationWithValueGreaterThanBoundaryWithExclusiveBoundary() {
        boolean inclusive = false;
        testAnnotationWithValueGreaterThanBoundary(inclusive);
    }

    private void testAnnotationWithValueGreaterThanBoundary(boolean inclusive) {
        String minDecAmount = "0.125";
        MonetaryAmountDecimalMinValidator validator = getValidator(minDecAmount, inclusive);
        boolean valid = validator.isValid(ofUSD(0.2), null);
        assertEquals("MinDecimal after boundary, inclusive = " + inclusive, true, valid);
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
