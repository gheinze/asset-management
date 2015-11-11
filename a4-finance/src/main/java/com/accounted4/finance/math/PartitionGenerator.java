package com.accounted4.finance.math;

import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.BiFunction;
import javax.money.MonetaryAmount;

/**
 * Tool for distributing money among a collection of partitions.
 *
 * @author gheinze
 */
public class PartitionGenerator {

    /**
     * Methods for distributing monetary amounts into Partitions when partitioning.
     */
    public enum Distribution {

        /**
         * No amount differs by more than a penny, with all extra pennies distributed among
         * the early partitions.
         * Ex. 50/6 = 8.333 => [8.34, 8.34, 8.33, 8.33, 8.33, 8.33]
         */
        RoundRobin((partitionAttributes, index) -> {
            return index < partitionAttributes.getRemainderInFractionalUnits()
                    ? partitionAttributes.getPartitionSizeFurthestFromZero()
                    : partitionAttributes.getPartitionSizeClosestToZero();
        })

        /**
         * An "adjusted" divide: the division is always force "UP" and the final amount is adjusted "DOWN" to compensate
         * for the fractional pennies overpayment.
         * Usage: amortization schedule.
         * Ex. 50/6 = 8.333 => [8.34, 8.34, 8.34, 8.34, 8.34, 8.30]
         */
        ,LastPartitionAdjustedDown((partitionAttributes, index) -> {
            int lastPartitionIndex = partitionAttributes.getNumberOfPartitions() - 1;
            if (index == lastPartitionIndex) {
                return partitionAttributes.getDistributedAmount().subtract(
                        partitionAttributes.getPartitionSizeFurthestFromZero().multiply(lastPartitionIndex));
            }
            return partitionAttributes.getPartitionSizeFurthestFromZero();
        })
        ;

        private final BiFunction<PartitionAttributes, Integer, MonetaryAmount> nextItemCalculator;

        private Distribution(BiFunction<PartitionAttributes, Integer, MonetaryAmount> nextItemCalculator) {
            this.nextItemCalculator = nextItemCalculator;
        }

    }



    private final PartitionAttributes attributes;


    public PartitionGenerator(MonetaryAmount amount, int numberOfPartitions) {
        this.attributes = new PartitionAttributes(amount, numberOfPartitions);
    }


    public Collection<MonetaryAmount> getPartitions(Distribution distribution) {

        return new AbstractCollection<MonetaryAmount>() {

            @Override
            public Iterator<MonetaryAmount> iterator() {
                return new PartitionIterator(distribution);
            }

            @Override
            public int size() {
                return attributes.getNumberOfPartitions();
            }

        };
    }


    private class PartitionIterator implements Iterator<MonetaryAmount> {

        private final Distribution distribution;
        private int currentIndex = 0;

        PartitionIterator(Distribution distribution) {
            this.distribution = distribution;
        }


        @Override
        public boolean hasNext() {
            return currentIndex < attributes.getNumberOfPartitions();
        }

        @Override
        public MonetaryAmount next() {
            if (!hasNext()) {
                throw new NoSuchElementException("Monetary partition iterator");
            }
            return distribution.nextItemCalculator.apply(attributes, currentIndex++);
        }

    }


}
