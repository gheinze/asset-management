package com.accounted4.assetmanager.core.address;

import com.accounted4.assetmanager.AbstractEntity;
import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import lombok.Getter;

/**
 *
 * @author gheinze
 */
@Getter
@Entity
public class CountrySubdivision extends AbstractEntity {

    private static final long serialVersionUID = 1L;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "country_id", nullable = false, updatable = false)
    Country country;

    String subdivisionCode;
    String subdivisionName;

    @Override
    public String toString() {
        return subdivisionCode + " - " + subdivisionName;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 83 * hash + Objects.hashCode(country.getCountryCode());
        hash = 83 * hash + Objects.hashCode(subdivisionCode);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final CountrySubdivision other = (CountrySubdivision) obj;
        if (!Objects.equals(country, other.getCountry())) {
            return false;
        }
        if (!Objects.equals(this.subdivisionCode, other.subdivisionCode)) {
            return false;
        }
        return true;
    }

}
