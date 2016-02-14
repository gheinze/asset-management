package com.accounted4.assetmanager.finance.gl;

import com.accounted4.assetmanager.AbstractEntity;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author gheinze
 */
@Getter @Setter
@Entity
public class GlTagHierarchy extends AbstractEntity {

    @OneToOne
    @JoinColumn(name = "gl_account_type_id")
    private GlAccountType glAccountType;

    private String name;
    private String description;

    @OneToMany
    @JoinTable(name = "gl_tag_hierarchy_tag_map",
            joinColumns = { @JoinColumn(name = "gl_tag_hierarchy_id", referencedColumnName="id") },
            inverseJoinColumns = { @JoinColumn(name = "gl_account_tag_id", referencedColumnName="id") }
    )
    private List<GlAccountTag> tags;

}
