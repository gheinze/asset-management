package com.accounted4.assetmanager.finance.gl;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author gheinze
 */
public interface GlTransactionRepository extends JpaRepository<GlTransaction, Long> {

}
