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

    private String chargeType;
    private boolean capitalizing;
    private int sortOrder;

    @Override
    public String toString() {
        return chargeType;
    }

}
