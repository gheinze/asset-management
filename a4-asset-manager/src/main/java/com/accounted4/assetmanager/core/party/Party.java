package com.accounted4.assetmanager.core.party;


import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 *
 * @author gheinze
 */
@Data
@ToString
@RequiredArgsConstructor
@Entity
public class Party implements Serializable {

    private static final long serialVersionUID = 1L;

    private @Id @GeneratedValue Long id;
    private String partyName;
    private String notes;

}
