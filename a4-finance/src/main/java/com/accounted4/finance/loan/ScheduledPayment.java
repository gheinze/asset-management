package com.accounted4.finance.loan;

import java.time.LocalDate;
import javax.money.MonetaryAmount;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * A structure to hold the information for a payment which can represent a line
 * item in an amortization schedule
 *
 * @author gheinze
 */
@Getter
@Setter
@ToString
public class ScheduledPayment {

    private int paymentNumber;
    private LocalDate paymentDate;
    private MonetaryAmount interest;
    private MonetaryAmount principal;
    private MonetaryAmount balance;


    public MonetaryAmount getPayment() {
        return getInterest().add( getPrincipal() );
    }


}
