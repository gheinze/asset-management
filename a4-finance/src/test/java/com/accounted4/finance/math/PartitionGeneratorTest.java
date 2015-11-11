package com.accounted4.finance.math;

import com.accounted4.finance.math.PartitionGenerator.Distribution;
import java.util.Iterator;
import java.util.NoSuchElementException;
import javax.money.Monetary;
import javax.money.MonetaryAmount;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author gheinze
 */
public class PartitionGeneratorTest {

    private static MonetaryAmount USD50;
    private static MonetaryAmount JPY1000;


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

    }


    public PartitionGeneratorTest() {
    }


    @Test
    public void testGetPartitionsRoundRobinBalances() {
        testGetPartitionsLastPartitionAdjustedDownBalances(Distribution.RoundRobin);
    }

    @Test
    public void testGetPartitionsLastPartitionAdjustedDownBalances() {
        testGetPartitionsLastPartitionAdjustedDownBalances(Distribution.LastPartitionAdjustedDown);
    }

    private void testGetPartitionsLastPartitionAdjustedDownBalances(Distribution distribution) {
        MonetaryAmount originalAmount = USD50;
        PartitionGenerator partition = new PartitionGenerator(originalAmount, 6);
        MonetaryAmount sum = partition.getPartitions(distribution).stream()
                .reduce((a, b) -> a.add(b))
                .get()
                ;
        assertEquals("Partition sum equals original amount for distribution " + distribution, originalAmount, sum);
    }



    @Test
    public void testGetPartitionsRoundRobinPartitionsDifferByAtMostOneUnit() {

        MonetaryAmount originalAmount = USD50;
        PartitionGenerator partition = new PartitionGenerator(originalAmount, 6);

        MonetaryAmount min = partition.getPartitions(PartitionGenerator.Distribution.RoundRobin).stream()
                .min((a, b) -> { return a.compareTo(b); })
                .get()
                ;

        MonetaryAmount max = partition.getPartitions(PartitionGenerator.Distribution.RoundRobin).stream()
                .max((a, b) -> { return a.compareTo(b); })
                .get()
                ;

        MonetaryAmount actualDelta = max.subtract(min);

        int fractionDigits = originalAmount.getCurrency().getDefaultFractionDigits();
        MonetaryAmount expectedDelta =
                Monetary.getDefaultAmountFactory()
                .setCurrency(originalAmount.getCurrency())
                .setNumber(1)
                .create()
                .scaleByPowerOfTen(fractionDigits * -1)
                ;

        assertEquals("Partion elements in a Round Robin distribution should differ by at most 1 fractional unit", expectedDelta, actualDelta);
    }

    @Test
    public void testGetPartitionsRoundRobinPartitionsLastItemIsSmallest() {
        checkLastPartitionIsSmallest(Distribution.RoundRobin);
    }

    @Test
    public void testGetPartitionsAdjustedDownLastItemIsSmallest() {
        checkLastPartitionIsSmallest(Distribution.LastPartitionAdjustedDown);
    }

    private void checkLastPartitionIsSmallest(Distribution distribution) {

        MonetaryAmount originalAmount = USD50;
        PartitionGenerator partition = new PartitionGenerator(originalAmount, 6);

        MonetaryAmount min = null;
        MonetaryAmount last = null;

        for (MonetaryAmount amount : partition.getPartitions(distribution)) {
            if (null == min || amount.isLessThan(min)) {
                min = amount;
            }
            last = amount;
        }

        assertTrue("Last partion element should be less than or equal to others", last.isLessThanOrEqualTo(min));
    }


    @Test(expected = NoSuchElementException.class)
    public void testGetPartitionsThrowsWhenTryingToReadBeyondBounds() {

        MonetaryAmount originalAmount = USD50;
        int partitions = 6;
        PartitionGenerator partition = new PartitionGenerator(originalAmount, partitions);
        Iterator<MonetaryAmount> it = partition.getPartitions(PartitionGenerator.Distribution.RoundRobin).iterator();

        for (int i = 0; i <= partitions; i++) {
            it.next();
        }

        fail("Partition collection should throw exception when accessing partition beyond collection size.");

    }


    @Test
    public void testGetPartitionsAdjustedDownAllSimilarExcludingLast() {

        MonetaryAmount originalAmount = USD50;
        int partitions = 6;
        PartitionGenerator partition = new PartitionGenerator(originalAmount, partitions);

        MonetaryAmount firstElement = null;
        int iterationIndex = 0;

        for (MonetaryAmount amount : partition.getPartitions(Distribution.LastPartitionAdjustedDown)) {
            if (null == firstElement) {
                firstElement = amount;
            }
            if (iterationIndex < partitions - 1) {
                assertEquals("Only last partition of LastPartitionAdjustedDown should vary index " + iterationIndex, firstElement, amount);
            } else {
                assertTrue("Last element cannot be larger than previous elements", amount.isLessThanOrEqualTo(firstElement));

            }
            iterationIndex++;
        }

    }



}
