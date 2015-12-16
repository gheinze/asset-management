package com.accounted4.assetmanager;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Version;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;

/**
 * All the JPA Entities for the schema have an id, version, and inactive flag collected
 * into this superclass for convenience.
 *
 * @author gheinze
 */
@MappedSuperclass
public class AbstractEntity implements Serializable {

    private @Id @GeneratedValue(strategy=GenerationType.IDENTITY) Long id;
    private @Version @Generated(GenerationTime.ALWAYS) Integer version;
    @Getter @Setter protected Boolean inactive;

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 41 * hash + Objects.hashCode(this.id);
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
        final AbstractEntity other = (AbstractEntity) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        return true;
    }


}
