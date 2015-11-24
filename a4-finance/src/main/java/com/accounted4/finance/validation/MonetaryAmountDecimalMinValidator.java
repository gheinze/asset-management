package com.accounted4.finance.validation;

import java.math.BigDecimal;
import javax.money.MonetaryAmount;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.constraints.DecimalMin;

/**
 *
 * @author gheinze
 */
public class MonetaryAmountDecimalMinValidator implements ConstraintValidator<DecimalMin, MonetaryAmount> {

    private BigDecimal minValue;
    private boolean inclusive;

    @Override
    public void initialize(DecimalMin constraintAnnotation) {
        this.minValue = new BigDecimal(constraintAnnotation.value());
        this.inclusive = constraintAnnotation.inclusive();
    }

    @Override
    public boolean isValid(MonetaryAmount value, ConstraintValidatorContext context) {
        if (null == value) {
            return false;
        }
        final BigDecimal amount = value.getNumber().numberValueExact(BigDecimal.class);
        int comparisonResult = amount.compareTo(minValue);
        return inclusive ? comparisonResult >= 0 : comparisonResult > 0;
    }

}
