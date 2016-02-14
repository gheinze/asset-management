package com.accounted4.assetmanager.finance.gl;

import com.accounted4.assetmanager.AbstractEntity;
import javax.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author gheinze
 */
@Getter @Setter
@Entity
public class GlAccountTag extends AbstractEntity {

    private String name;
    private boolean asset;
    private boolean liability;
    private boolean equity;
    private boolean revenue;
    private boolean expense;
    private String description;

}
