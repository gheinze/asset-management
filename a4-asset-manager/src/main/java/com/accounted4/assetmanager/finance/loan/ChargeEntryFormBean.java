package com.accounted4.assetmanager.finance.loan;

import java.time.LocalDate;
import java.util.List;
import javax.money.MonetaryAmount;
import lombok.Getter;
import lombok.Setter;

/**
 * Backing bean for a ChargeEntryForm
 * @author gheinze
 */
@Getter @Setter
public class ChargeEntryFormBean {

    private boolean editMode;
    private List<LoanChargeType> chargeTypes;

    private LoanChargeType chargeType;
    private MonetaryAmount amount;
    private LocalDate chargeDate;
    private String note;

}
