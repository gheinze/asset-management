package com.accounted4.assetmanager.useraccount;

import javax.inject.Inject;
import org.springframework.stereotype.Service;

/**
 *
 * @author gheinze
 */
@Service
public class UserAccountServiceImpl implements UserAccountService {

    @Inject UserAccountRepository userAccountRepo;


    @Override
    public UserAccount authenticate(String userAccount, String password) {
        return userAccountRepo.findUserAccountByNameAndPassword(userAccount, password);
    }

}
