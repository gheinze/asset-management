package com.accounted4.assetmanager.repository;

import com.accounted4.assetmanager.entity.LoanNote;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author gheinze
 */
public interface LoanNoteRepository extends JpaRepository<LoanNote, Long> {
}
