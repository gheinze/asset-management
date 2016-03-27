package com.accounted4.assetmanager.useraccount;

import com.accounted4.assetmanager.entity.UserAccount;

/**
 *
 * @author gheinze
 */
interface UserAccountRepositoryExt {

    UserAccount findUserAccountByNameAndPassword(String userAccountName, String password);

}
