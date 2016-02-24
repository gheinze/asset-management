package com.accounted4.assetmanager.finance.loan;

import com.accounted4.assetmanager.AbstractEntity;
import javax.persistence.Entity;
import lombok.Getter;

/**
 *
 * @author gheinze
 */
@Getter
@Entity
public class LoanChargeType extends AbstractEntity {

    // Todo: loan service should cache values pulled from db an offer them up
    // This should be yanked away.
    public static LoanChargeType getOtherCapitalizing() {
        LoanChargeType type = new LoanChargeType();
        type.chargeType = "Other (capitalizing)";
        type.capitalizing = true;
        return type;
    }

    private String chargeType;
    private boolean capitalizing;
    private int sortOrder;

    @Override
    public String toString() {
        return chargeType;
    }

}
