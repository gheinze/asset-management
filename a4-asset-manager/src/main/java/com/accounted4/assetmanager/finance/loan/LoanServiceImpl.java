package com.accounted4.assetmanager.finance.loan;

import com.accounted4.finance.loan.AmortizationAttributes;
import com.accounted4.finance.loan.AmortizationCalculator;
import com.accounted4.finance.loan.ScheduledPayment;
import java.util.List;
import javax.money.MonetaryAmount;
import org.springframework.stereotype.Service;

/**
 *
 * @author gheinze
 */
@Service
public class LoanServiceImpl implements LoanService {

    @Override
    public MonetaryAmount getPeriodicPayment(AmortizationAttributes amAttrs) {
        return AmortizationCalculator.getPeriodicPayment(amAttrs);
    }

    @Override
    public List<ScheduledPayment> generateSchedule(AmortizationAttributes amAttrs) {
        return AmortizationCalculator.generateSchedule(amAttrs);
    }

}
