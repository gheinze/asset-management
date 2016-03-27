package com.accounted4.assetmanager.useraccount;

/**
 *
 * @author gheinze
 */
public interface UserAccountService {

    UserAccount authenticate(String userAccount, String password);

}
