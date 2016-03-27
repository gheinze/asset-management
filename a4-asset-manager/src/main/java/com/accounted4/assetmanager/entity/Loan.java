package com.accounted4.assetmanager.entity;


import java.util.Objects;
import java.util.Set;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
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


    @OneToMany(mappedBy = "loan", fetch = FetchType.EAGER, orphanRemoval = true)
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    private Set<Cheque> cheques;


    @OneToMany(mappedBy = "loan", fetch = FetchType.EAGER, orphanRemoval = true)
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    private Set<LoanPayment> payments;


    @OneToMany(mappedBy = "loan", fetch = FetchType.EAGER, orphanRemoval = true)
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    private Set<LoanCharge> charges;


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
        if (!loanName.equals(other.getLoanName())) {
            return false;
        }
        return true;
    }


    public void removeCheque(Cheque cheque) {
        cheques.remove(cheque);
        cheque.setLoan(null);
    }

}
