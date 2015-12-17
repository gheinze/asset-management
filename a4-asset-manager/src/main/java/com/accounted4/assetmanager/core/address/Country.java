package com.accounted4.assetmanager.core.address;

import com.accounted4.assetmanager.AbstractEntity;
import javax.persistence.Entity;
import lombok.Getter;

/**
 *
 * @author gheinze
 */
@Getter
@Entity
public class Country extends AbstractEntity {

    private static final long serialVersionUID = 1L;

    private String countryCode;
    private String countryName;


    @Override
    public String toString() {
        return countryCode + " - " + countryName;
    }

}
