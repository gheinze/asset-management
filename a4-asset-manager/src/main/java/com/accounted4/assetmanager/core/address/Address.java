package com.accounted4.assetmanager.core.address;

import com.accounted4.assetmanager.AbstractEntity;
import com.accounted4.assetmanager.core.party.Party;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author gheinze
 */
@Getter @Setter
@Entity
public class Address extends AbstractEntity {

    private static final long serialVersionUID = 1L;

    private String line1;
    private String line2;
    private String city;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "country_subdivision_id", nullable = false, updatable = false)
    private CountrySubdivision countrySubdivision;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "country_id", nullable = false, updatable = false)
    private Country country;

    private String postalCode;
    private String note;

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "addresses")
    private Set<Party> parties = new HashSet<>(0);



    public String getAddress() {
        return this.toString();
    }


    private static final String SEPARATOR = ", ";

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        sb.append(null == line1 ? "" : line1 + SEPARATOR);
        sb.append(null == line2 ? "" : line2 + SEPARATOR);
        sb.append(null == city ? "" : city + SEPARATOR);
        sb.append(null == countrySubdivision ? "" : countrySubdivision.getSubdivisionCode() + SEPARATOR);
        sb.append(null == country ? "" : country.getCountryCode() + SEPARATOR);
        sb.append(null == postalCode ? "" : postalCode + SEPARATOR);

        String result = sb.toString();
        if (result.endsWith(SEPARATOR)) {
            result = result.substring(0, result.length() - SEPARATOR.length());
        }

        return result;
    }


}
