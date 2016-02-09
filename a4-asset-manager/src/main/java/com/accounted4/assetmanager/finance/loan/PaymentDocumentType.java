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
public class PaymentDocumentType extends AbstractEntity {

    private String documentType;
    private String description;


    @Override
    public String toString() {
        return documentType;
    }

}
