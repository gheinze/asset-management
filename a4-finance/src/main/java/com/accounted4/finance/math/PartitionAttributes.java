package com.accounted4.finance.math;

import javax.money.Monetary;
import javax.money.MonetaryAmount;
import lombok.Getter;


/**
 *
 * @author gheinze
 */
@Getter
public class PartitionAttributes {


    private final int numberOfPartitions;
    private final MonetaryAmount distributedAmount;
    private final MonetaryAmount partitionSizeClosestToZero;
    private final MonetaryAmount partitionSizeFurthestFromZero;
    private final MonetaryAmount remainder;
    private final long remainderInFractionalUnits;


    public PartitionAttributes(MonetaryAmount amount, int numberOfPartitions) {

        validateArguments(amount, numberOfPartitions);

        this.distributedAmount = amount;
        this.numberOfPartitions = numberOfPartitions;

        MonetaryAmount[] divideResult = divideAndRemainderWithCurrencyFractionalUnits(amount, numberOfPartitions);
        partitionSizeClosestToZero = divideResult[0];
        remainder = divideResult[1];
        remainderInFractionalUnits = scaleUpToCurrencyFractionalUnits(remainder).getNumber().longValueExact();
        partitionSizeFurthestFromZero = remainder.isZero() ? partitionSizeClosestToZero : addOneFractionalUnitTo(partitionSizeClosestToZero);

    }


    private void validateArguments(MonetaryAmount amount, int numberOfPartitions) {

        if (null == amount) {
            throw new IllegalArgumentException("Monetary amount may not be null");
        }

        if (numberOfPartitions <= 0) {
            throw new IllegalArgumentException("Number of partitions for the split must be > 0");
        }

    }


    private MonetaryAmount[] divideAndRemainderWithCurrencyFractionalUnits(MonetaryAmount amount, long numberOfPartitions) {
        MonetaryAmount[] scaledUpDivideResult = scaleUpToCurrencyFractionalUnits(amount).divideAndRemainder(numberOfPartitions);
        int fractionDigits = amount.getCurrency().getDefaultFractionDigits();
        MonetaryAmount[] divideResult = {
            scaledUpDivideResult[0].scaleByPowerOfTen(fractionDigits * -1),
            scaledUpDivideResult[1].scaleByPowerOfTen(fractionDigits * -1)
        };
        return divideResult;
    }


    private MonetaryAmount scaleUpToCurrencyFractionalUnits(MonetaryAmount amount) {
        return amount.scaleByPowerOfTen(amount.getCurrency().getDefaultFractionDigits());
    }


    private MonetaryAmount addOneFractionalUnitTo(MonetaryAmount partitionSizeClosestToZero) {

        int fractionDigits = partitionSizeClosestToZero.getCurrency().getDefaultFractionDigits();

        MonetaryAmount oneFractionalUnit = Monetary.getDefaultAmountFactory()
                .setCurrency(partitionSizeClosestToZero.getCurrency())
                .setNumber(1L)
                .create()
                .scaleByPowerOfTen(fractionDigits * -1);

        return partitionSizeClosestToZero.add(oneFractionalUnit);

    }


}
