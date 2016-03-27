package com.accounted4.assetmanager.useraccount;

/**
 *
 * @author gheinze
 */
interface UserAccountRepositoryExt {

    UserAccount findUserAccountByNameAndPassword(String userAccountName, String password);

}
