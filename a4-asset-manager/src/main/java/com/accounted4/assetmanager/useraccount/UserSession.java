package com.accounted4.assetmanager.useraccount;

import lombok.Data;

/**
 *
 * @author gheinze
 */
@Data
public class UserSession {

    public static final String USER_SESSION_KEY = "userSession";

    private String userAccountName;
    private String displayName;
    private String tenant;

}
