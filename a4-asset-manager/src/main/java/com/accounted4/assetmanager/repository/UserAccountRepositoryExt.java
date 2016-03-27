package com.accounted4.assetmanager.repository;

import com.accounted4.assetmanager.entity.UserAccount;

/**
 *
 * @author gheinze
 */
interface UserAccountRepositoryExt {

    UserAccount findUserAccountByNameAndPassword(String userAccountName, String password);

}
