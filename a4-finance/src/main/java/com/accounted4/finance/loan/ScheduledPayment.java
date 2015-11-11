package com.accounted4.finance.loan;

import java.time.LocalDate;
import java.util.Objects;
import javax.money.MonetaryAmount;
import lombok.Getter;
import lombok.Setter;

/**
 * A structure to hold the information for a payment which can represent a line
 * item in an amortization schedule
 *
 * @author gheinze
 */
@Getter
@Setter
public class ScheduledPayment {

    private int paymentNumber;
    private LocalDate paymentDate;
    private MonetaryAmount interest;
    private MonetaryAmount principal;
    private MonetaryAmount balance;



    @Override
    public String toString() {
        return "ScheduledPayment{" + "paymentNumber=" + paymentNumber + ", paymentDate=" + paymentDate + ", interest=" + interest + ", principal=" + principal + ", balance=" + balance + '}';
    }

    public MonetaryAmount getPayment() {
        return getInterest().add( getPrincipal() );
    }


}
