package com.accounted4.assetmanager.useraccount;

import com.accounted4.assetmanager.entity.UserAccount;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author gheinze
 */
public class UserAccountRepositoryImpl implements UserAccountRepositoryExt {

    @PersistenceContext
    private EntityManager em;



    private static final String JPQL_FOR_FIND_ACCOUNT =
            "SELECT u FROM UserAccount u " +
            "  WHERE name = :userAccountName " +
            "    AND encrypted_password = pgcrypto.crypt( concat(:userAccountName2, :password), encrypted_password)" +
            "    AND status = 'ACTIVE'"
            ;


    @Override
    public UserAccount findUserAccountByNameAndPassword(String userAccountName, String password) {
        return em
                .createQuery(JPQL_FOR_FIND_ACCOUNT, UserAccount.class)
                .setParameter("userAccountName", userAccountName)
                .setParameter("userAccountName2", userAccountName)
                .setParameter("password", password)
                .getSingleResult()
                ;
    }

}
