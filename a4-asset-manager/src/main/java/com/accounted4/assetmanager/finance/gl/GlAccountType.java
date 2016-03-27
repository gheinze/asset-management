package com.accounted4.assetmanager.finance.gl;

import com.accounted4.assetmanager.entity.AbstractEntity;
import javax.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author gheinze
 */
@Getter @Setter
@Entity
public class GlAccountType extends AbstractEntity {

    private String glAccountType;
    private String naturalBalance;
    private int sortOrder;
    private String displayName;

}
