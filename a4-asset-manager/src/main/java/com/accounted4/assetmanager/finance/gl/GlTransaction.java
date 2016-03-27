package com.accounted4.assetmanager.finance.gl;

import com.accounted4.assetmanager.entity.AbstractEntity;
import java.time.LocalDate;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author gheinze
 */
@Getter @Setter
@Entity
public class GlTransaction extends AbstractEntity {

    private LocalDate transactionDate;

    @OneToMany(mappedBy = "glTransaction")
    private List<GlTransactionDetail> transactionDetails;

    private String description;

}
