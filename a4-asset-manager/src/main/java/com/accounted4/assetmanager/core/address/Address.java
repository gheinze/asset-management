package com.accounted4.assetmanager.core.address;

import com.accounted4.assetmanager.core.party.Party;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Version;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;

/**
 *
 * @author gheinze
 */
@Getter @Setter
@Entity
public class Address  implements Serializable {

    private static final long serialVersionUID = 1L;

    private @Id @GeneratedValue(strategy=GenerationType.IDENTITY) Long id;
    private @Version @Generated(GenerationTime.ALWAYS) Integer version;
    private Boolean inactive;

    private String line1;
    private String line2;
    private String city;
    private String subdivisionCode;
    private String country;
    private String postalCode;
    private String note;

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "addresses")
    private Set<Party> parties = new HashSet<>(0);



    private static final String SEPARATOR = ", ";

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        sb.append(null == line1 ? "" : line1 + SEPARATOR);
        sb.append(null == line2 ? "" : line2 + SEPARATOR);
        sb.append(null == city ? "" : city + SEPARATOR);
        sb.append(null == subdivisionCode ? "" : subdivisionCode + SEPARATOR);
        sb.append(null == country ? "" : country + SEPARATOR);
        sb.append(null == postalCode ? "" : postalCode + SEPARATOR);
        sb.append(null == note ? "" : note);

        String result = sb.toString();
        if (result.endsWith(SEPARATOR)) {
            result = result.substring(0, result.length() - SEPARATOR.length());
        }

        return result;
    }


}
