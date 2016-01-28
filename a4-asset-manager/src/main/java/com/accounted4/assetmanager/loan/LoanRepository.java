package com.accounted4.assetmanager.loan;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author gheinze
 */
public interface LoanRepository extends JpaRepository<Loan, Long> {

	List<Loan> findByInactiveOrderByLoanName(boolean inactive);

}
