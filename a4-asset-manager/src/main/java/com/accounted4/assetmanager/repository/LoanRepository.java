package com.accounted4.assetmanager.repository;

import com.accounted4.assetmanager.entity.Loan;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author gheinze
 */
public interface LoanRepository extends JpaRepository<Loan, Long>, LoanRepositoryLov {

    List<Loan> findByInactiveOrderByLoanName(boolean inactive);

    List<Loan> findByCloseDateIsNull();
        
}
