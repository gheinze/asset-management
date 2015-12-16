package com.accounted4.assetmanager.core.address;

import com.accounted4.assetmanager.AbstractEntity;
import java.io.Serializable;
import javax.persistence.Entity;
import lombok.Getter;

/**
 *
 * @author gheinze
 */
@Getter
@Entity
public class Country extends AbstractEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    private String countryCode;
    private String countryName;


    @Override
    public String toString() {
        return countryCode + " - " + countryName;
    }

}
