package com.accounted4.assetmanager.repository;

import com.accounted4.assetmanager.entity.Party;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author gheinze
 */
public interface PartyRepository extends JpaRepository<Party, Long> {

	List<Party> findByInactiveOrderByPartyName(boolean inactive);

}
