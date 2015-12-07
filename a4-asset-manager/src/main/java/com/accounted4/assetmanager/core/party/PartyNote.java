package com.accounted4.assetmanager.core.party;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
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
public class PartyNote {

    private static final long serialVersionUID = 1L;

    private @Id @GeneratedValue(strategy=GenerationType.IDENTITY) Long id;
    private @Version @Generated(GenerationTime.ALWAYS) Integer version;

    @OneToOne
    @JoinColumn(name="party_id")
    private Party party;
    private String note;

}
