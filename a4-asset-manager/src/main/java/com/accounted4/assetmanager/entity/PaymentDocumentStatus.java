package com.accounted4.assetmanager.entity;

import javax.persistence.Entity;
import lombok.Getter;

/**
 *
 * @author gheinze
 */
@Getter
@Entity
public class PaymentDocumentStatus extends AbstractEntity {

    private String documentStatus;
    private String description;

    @Override
    public String toString() {
        return documentStatus;
    }


}
