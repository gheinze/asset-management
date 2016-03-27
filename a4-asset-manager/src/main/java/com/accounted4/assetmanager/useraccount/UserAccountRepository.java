package com.accounted4.assetmanager.useraccount;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author gheinze
 */
public interface UserAccountRepository extends JpaRepository<UserAccount, Long>, UserAccountRepositoryExt {

}
