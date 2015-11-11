package com.accounted4.finance.math;

import java.math.BigDecimal;
import javax.money.CurrencyUnit;
import javax.money.Monetary;
import javax.money.MonetaryAmount;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.BeforeClass;

/**
 *
 * @author gheinze
 */
public class PartitionAttributesTest {

    private static MonetaryAmount USD50;
    private static MonetaryAmount JPY1000;

    private static int somePositiveNumberOfPartitions;


    @BeforeClass
    public static void preCreateCommonInstances() {

        USD50 = Monetary.getDefaultAmountFactory()
                .setCurrency("USD")
                .setNumber(50)
                .create();

        JPY1000 = Monetary.getDefaultAmountFactory()
                .setCurrency("JPY")
                .setNumber(1000)
                .create();

        somePositiveNumberOfPartitions = 99;

    }


    public PartitionAttributesTest() {
    }


    @Test(expected = IllegalArgumentException.class)
    public void testConstructorWithNullMoney() {
        MonetaryAmount amountToBePartitioned = null;
        int numberOfPartitions = 1;
        new PartitionAttributes(amountToBePartitioned, numberOfPartitions);
        fail("A null monetary amount cannot be partitioned.");
    }


    @Test(expected = IllegalArgumentException.class)
    public void testConstructorWithNegativeNumberOfPartitions() {
        int numberOfPartitions = -1;
        splitAMonetaryAmountIntoPartitions(numberOfPartitions);
        fail("A monetary amount can not be split into less than 1 partition.");
    }


    @Test(expected = IllegalArgumentException.class)
    public void testConstructorWithZeroNumberOfPartitions() {
        int numberOfPartitions = 0;
        splitAMonetaryAmountIntoPartitions(numberOfPartitions);
        fail("A monetary amount can not be split into 0 partitions.");
    }


    private void splitAMonetaryAmountIntoPartitions(int numberOfPartitions) {
        splitAMonetaryAmountIntoPartitions(USD50, numberOfPartitions);
    }


    private void splitAMonetaryAmountIntoPartitions(MonetaryAmount amount, int numberOfPartitions) {
        new PartitionAttributes(amount, numberOfPartitions);
    }


    @Test
    public void testConstructorWithPositiveAmount() {
        splitAMonetaryAmountIntoPartitions(USD50, somePositiveNumberOfPartitions);
        assertTrue("A positive monetary amount splits into partitions", true);
    }


    @Test
    public void testConstructorWithNegativeAmount() {
        splitAMonetaryAmountIntoPartitions(USD50.negate(), somePositiveNumberOfPartitions);
        assertTrue("A negative monetary amount splits into partitions", true);
    }


    @Test
    public void testGetPartitionSizeClosestToZeroHasConsistentCurrency() {

        int numberOfPartitions = 6;
        PartitionAttributes partition = new PartitionAttributes(USD50, numberOfPartitions);
        MonetaryAmount smallestMagnitude = partition.getPartitionSizeClosestToZero();
        assertEquals("Currency unit should match orignal currency unit", USD50.getCurrency(), smallestMagnitude.getCurrency());

        partition = new PartitionAttributes(JPY1000, numberOfPartitions);
        smallestMagnitude = partition.getPartitionSizeClosestToZero();
        assertEquals("Currency unit should match orignal currency unit, varried to avoid system default dependency", JPY1000.getCurrency(), smallestMagnitude.getCurrency());

    }


    @Test
    public void testGetPartitionSizeClosestToZero() {

        int numberOfPartitions = 6;
        PartitionAttributes partition = new PartitionAttributes(USD50, numberOfPartitions);
        MonetaryAmount partitionSize = partition.getPartitionSizeClosestToZero(); // from 8.333...

        MonetaryAmount USD8Point33 =
                Monetary.getDefaultAmountFactory()
                .setCurrency(USD50.getCurrency())
                .setNumber(new BigDecimal("8.33"))
                .create();

        assertEquals("Small bucket should truncate fractional digits", USD8Point33, partitionSize);

    }


    @Test
    public void testGetPartitionSizeClosestToZeroNegative() {

        int numberOfPartitions = 6;
        PartitionAttributes partition = new PartitionAttributes(USD50.negate(), numberOfPartitions);
        MonetaryAmount partitionSize = partition.getPartitionSizeClosestToZero(); // from 8.333...

        MonetaryAmount NegativeUSD8Point33 =
                Monetary.getDefaultAmountFactory()
                .setCurrency(USD50.getCurrency())
                .setNumber(new BigDecimal("8.33"))
                .create()
                .negate();

        assertEquals("Small bucket should truncate fractional digits", NegativeUSD8Point33, partitionSize);

    }


    @Test
    public void testGetPartitionSizeClosestToZeroTruncatedNotRounded() {

        int numberOfPartitions = 3;
        PartitionAttributes partition = new PartitionAttributes(USD50, numberOfPartitions);
        MonetaryAmount floor = partition.getPartitionSizeClosestToZero();  // from 16.666...

        MonetaryAmount USD16Point66 =
                Monetary.getDefaultAmountFactory()
                .setCurrency(USD50.getCurrency())
                .setNumber(new BigDecimal("16.66"))
                .create();

        assertEquals("PartitionSizeClosestToZero should truncate to fractional digits, no rounding", USD16Point66, floor);

    }

    @Test
    public void testGetPartitionSizeClosestToZeroExact() {

        CurrencyUnit USCurrencyUnit = Monetary.getCurrency("USD");

        MonetaryAmount USD51 =
                Monetary.getDefaultAmountFactory()
                .setCurrency(USCurrencyUnit)
                .setNumber(51.00)
                .create();

        int numberOfPartitions = 2;

        PartitionAttributes partition = new PartitionAttributes(USD51, numberOfPartitions);
        MonetaryAmount floor = partition.getPartitionSizeClosestToZero();

        MonetaryAmount USD25Point50 =
                Monetary.getDefaultAmountFactory()
                .setCurrency(USCurrencyUnit)
                .setNumber(new BigDecimal("25.50"))
                .create();

        assertEquals("PartitionSizeClosestToZero for exact divide", USD25Point50, floor);

    }

    @Test
    public void testGetPartitionSizeClosestToZeroForCurrencyWithAlternateFractionalDigits() {

        int numberOfPartitions = 3;
        PartitionAttributes partition = new PartitionAttributes(JPY1000, numberOfPartitions);
        MonetaryAmount floor = partition.getPartitionSizeClosestToZero();  // from 333.333...

        MonetaryAmount JPY333 =
                Monetary.getDefaultAmountFactory()
                .setCurrency(JPY1000.getCurrency())
                .setNumber(new BigDecimal("333"))
                .create();

        assertEquals("PartitionSizeClosestToZero should truncate to number of fractional digits of a non-default currency", JPY333, floor);

    }


    @Test
    public void testGetPartitionSizeClosestToZeroForNegativeAmount() {

        int numberOfPartitions = 3;
        PartitionAttributes partition = new PartitionAttributes(USD50.negate(), numberOfPartitions);
        MonetaryAmount floor = partition.getPartitionSizeClosestToZero();  // from -16.666...

        MonetaryAmount USDMinus16Point66 =
                Monetary.getDefaultAmountFactory()
                .setCurrency(USD50.getCurrency())
                .setNumber(new BigDecimal("-16.66"))
                .create();

        assertEquals("PartitionSizeClosestToZero of negative", USDMinus16Point66, floor);
    }


    @Test
    public void testGetRemainder() {
        int numberOfPartitions = 6;
        PartitionAttributes partition = new PartitionAttributes(USD50, numberOfPartitions);
        MonetaryAmount expectedRemainder = USD50.subtract(partition.getPartitionSizeClosestToZero().multiply(numberOfPartitions));
        assertEquals("Remainder is the missing amount from the sum based on the small partition sizes", expectedRemainder, partition.getRemainder());
    }


    @Test
    public void testGetRemainderNegative() {
        int numberOfPartitions = 6;
        PartitionAttributes partition = new PartitionAttributes(USD50.negate(), numberOfPartitions);
        MonetaryAmount expectedRemainder = USD50.negate().subtract(partition.getPartitionSizeClosestToZero().multiply(numberOfPartitions));
        assertEquals("Remainder is the missing amount from the sum based on the small partition sizes -negative", expectedRemainder, partition.getRemainder());
    }


    @Test
    public void testGetRemainderNegativeAlternateCurrency() {
        int numberOfPartitions = 120;
        PartitionAttributes partition = new PartitionAttributes(JPY1000.negate(), numberOfPartitions);
        MonetaryAmount expectedRemainder = JPY1000.negate().subtract(partition.getPartitionSizeClosestToZero().multiply(numberOfPartitions));
        assertEquals("Remainder is the missing amount from the sum based on the small partition sizes -negative/alt currency", expectedRemainder, partition.getRemainder());
    }


    @Test
    public void testGetRemainderHasConsistentCurrency() {
        int numberOfPartitions = 120;
        PartitionAttributes partition = new PartitionAttributes(JPY1000.negate(), numberOfPartitions);
        assertEquals("Remainder has consistent currency", JPY1000.getCurrency(), partition.getRemainder().getCurrency());
    }


    // furthest, at most 1 difference from closest
    @Test
    public void testGetPartitionSizeFurthestFromZeroIsOneGreaterThanClosest() {
        testGetPartitionSizeFurthestFromZeroIsOneGreaterThanClosest(USD50);
    }


    @Test
    public void testGetPartitionSizeFurthestFromZeroIsOneGreaterThanClosestNegative() {
        testGetPartitionSizeFurthestFromZeroIsOneGreaterThanClosest(USD50.negate());
    }


    private void testGetPartitionSizeFurthestFromZeroIsOneGreaterThanClosest(MonetaryAmount amount) {

        int numberOfPartitions = 3;
        PartitionAttributes partition = new PartitionAttributes(amount, numberOfPartitions);
        MonetaryAmount basePartition = partition.getPartitionSizeClosestToZero();
        MonetaryAmount expandedPartition = partition.getPartitionSizeFurthestFromZero();
        MonetaryAmount actualDelta = expandedPartition.subtract(basePartition);

        int fractionDigits = amount.getCurrency().getDefaultFractionDigits();
        MonetaryAmount expectedDelta = Monetary.getDefaultAmountFactory()
                .setCurrency(amount.getCurrency())
                .setNumber(1L)
                .create()
                .scaleByPowerOfTen(fractionDigits * -1);


        assertEquals("PartitionSizeFurthestFrom is at most 1 fractional unit different from closest", expectedDelta, actualDelta);

    }


    @Test
    public void testGetPartitionSizeFurthestFromZeroEvenDivideMatchesClosest() {

        CurrencyUnit USCurrencyUnit = Monetary.getCurrency("USD");

        MonetaryAmount USD51 =
                Monetary.getDefaultAmountFactory()
                .setCurrency(USCurrencyUnit)
                .setNumber(51.00)
                .create();

        int numberOfPartitions = 2;

        PartitionAttributes partition = new PartitionAttributes(USD51, numberOfPartitions);
        MonetaryAmount smallest = partition.getPartitionSizeClosestToZero();
        MonetaryAmount largest = partition.getPartitionSizeFurthestFromZero();

        assertEquals("PartitionSizeClosestToZero for exact divide", smallest, largest);

    }

    @Test
    public void testGetNumberOfPartitions() {
        int numberOfPartitions = 67;
        PartitionAttributes partition = new PartitionAttributes(USD50, numberOfPartitions);
        assertEquals("Partition count matches requested count", numberOfPartitions, partition.getNumberOfPartitions());
    }


    /**
     * 50 / 6 = 8.33
     *
     * remainder = 50.00 - (8.33*6)
     *           = 50.00 - 49.98
     *           = 0.02
     * USD fractional unit = 0.01
     * remainder in fractional units = 2
     */
    @Test
    public void testRemainderInFractionalUnits() {
        int numberOfPartitions = 6;
        PartitionAttributes partition = new PartitionAttributes(USD50, numberOfPartitions);
        assertEquals("Remainder in fractional units", 2, partition.getRemainderInFractionalUnits());
    }

    @Test
    public void testRemainderInFractionalUnitsMatchesScaledRemainder() {
        int numberOfPartitions = 6;
        PartitionAttributes partition = new PartitionAttributes(USD50, numberOfPartitions);

        int fractionDigits = USD50.getCurrency().getDefaultFractionDigits();
        int expected = partition.getRemainder().scaleByPowerOfTen(fractionDigits).getNumber().intValueExact();

        assertEquals("Remainder in fractional units is consistent with scaled remainder", expected, partition.getRemainderInFractionalUnits());
    }

    @Test
    public void testRemainderInFractionalUnitsMatchesScaledRemainderAlternteCurrency() {

        CurrencyUnit TNDCurrencyUnit = Monetary.getCurrency("TND");

        MonetaryAmount TND100 =
                Monetary.getDefaultAmountFactory()
                .setCurrency(TNDCurrencyUnit)
                .setNumber(100)
                .create();

        int numberOfPartitions = 3;

        PartitionAttributes partition = new PartitionAttributes(TND100, numberOfPartitions);
        int fractionDigits = TND100.getCurrency().getDefaultFractionDigits();
        int expected = partition.getRemainder().scaleByPowerOfTen(fractionDigits).getNumber().intValueExact();

        assertEquals("Remainder in fractional units is consistent with scaled remainder, alternate currency", expected, partition.getRemainderInFractionalUnits());

    }

    @Test
    public void testGetDistributedAmountMatchesOriginalAmount() {
        int numberOfPartitions = 3;
        PartitionAttributes partition = new PartitionAttributes(USD50, numberOfPartitions);
        assertEquals("The original amount is the amount to distribute", USD50, partition.getDistributedAmount());
    }

}
