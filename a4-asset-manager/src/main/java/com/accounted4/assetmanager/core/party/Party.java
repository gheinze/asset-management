package com.accounted4.assetmanager.core.party;


import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Version;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;

/**
 *
 * @author gheinze
 */
@Getter @Setter
@RequiredArgsConstructor
@Entity
public class Party implements Serializable {

    private static final long serialVersionUID = 1L;

    private @Id @GeneratedValue(strategy=GenerationType.IDENTITY) Long id;
    private @Version @Generated(GenerationTime.ALWAYS) Integer version;
    private String partyName;
    private Boolean inactive;

    @Override
    public String toString() {
        return partyName;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 47 * hash + Objects.hashCode(this.partyName);
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
        final Party other = (Party) obj;
        if (!Objects.equals(this.partyName, other.partyName)) {
            return false;
        }
        return true;
    }



}
