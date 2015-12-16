package com.accounted4.assetmanager.core.party;


import com.accounted4.assetmanager.AbstractEntity;
import com.accounted4.assetmanager.core.address.Address;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 *
 * @author gheinze
 */
@Getter @Setter
@RequiredArgsConstructor
@Entity
public class Party extends AbstractEntity {

    private static final long serialVersionUID = 1L;

    private String partyName;

    @OneToOne(mappedBy="party")
    private PartyNote note;


    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name="party_address",
            joinColumns = { @JoinColumn(name = "party_id", nullable = false, updatable = false) },
            inverseJoinColumns = { @JoinColumn(name = "address_id", nullable = false, updatable = false) }
    )
    private Set<Address> addresses = new HashSet<>(0);



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
