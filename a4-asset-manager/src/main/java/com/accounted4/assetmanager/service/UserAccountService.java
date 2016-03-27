package com.accounted4.assetmanager.service;

import com.accounted4.assetmanager.entity.UserAccount;

/**
 *
 * @author gheinze
 */
public interface UserAccountService {

    UserAccount authenticate(String userAccount, String password);

}
