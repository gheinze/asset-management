package com.accounted4.assetmanager.loan;


import com.accounted4.assetmanager.AbstractEntity;
import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.OneToOne;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Cascade;

/**
 *
 * @author gheinze
 */
@Getter @Setter
@RequiredArgsConstructor
@Entity
public class Loan extends AbstractEntity {

    private static final long serialVersionUID = 1L;

    private String loanName;

    @OneToOne(mappedBy="loan")
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    private LoanTerms terms;


    @Override
    public String toString() {
        return loanName;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 47 * hash + Objects.hashCode(this.loanName);
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
        final Loan other = (Loan) obj;
        if (!Objects.equals(this.loanName, other.loanName)) {
            return false;
        }
        return true;
    }

}
