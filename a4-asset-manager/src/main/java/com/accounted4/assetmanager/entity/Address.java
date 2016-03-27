package com.accounted4.assetmanager.entity;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;
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

    @NotNull
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

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.line1);
        hash = 97 * hash + Objects.hashCode(this.line2);
        hash = 97 * hash + Objects.hashCode(this.city);
        hash = 97 * hash + Objects.hashCode(this.countrySubdivision);
        hash = 97 * hash + Objects.hashCode(this.country);
        hash = 97 * hash + Objects.hashCode(this.postalCode);
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
        final Address other = (Address) obj;
        if (!Objects.equals(this.line1, other.line1)) {
            return false;
        }
        if (!Objects.equals(this.line2, other.line2)) {
            return false;
        }
        if (!Objects.equals(this.city, other.city)) {
            return false;
        }
        if (!Objects.equals(this.postalCode, other.postalCode)) {
            return false;
        }
        if (!Objects.equals(this.countrySubdivision, other.countrySubdivision)) {
            return false;
        }
        if (!Objects.equals(this.country, other.country)) {
            return false;
        }
        return true;
    }


}
