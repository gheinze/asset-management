/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.accounted4.assetmanager.finance.loan;

import com.accounted4.finance.loan.AmortizationAttributes;
import com.accounted4.finance.loan.ScheduledPayment;
import java.util.List;
import javax.money.MonetaryAmount;

/**
 *
 * @author gheinze
 */
public interface LoanService {

    MonetaryAmount getPeriodicPayment(AmortizationAttributes amAttrs);
    
    List<ScheduledPayment> generateSchedule(AmortizationAttributes amAttrs);

}
