package com.repnox.nineseventax.features.auth;

import java.io.Serializable;

public class AuthSession implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 7897483826011795495L;
	private AuthUser authUser;

    public AuthUser getAuthUser() {
        return authUser;
    }

    public void setAuthUser(AuthUser authUser) {
        this.authUser = authUser;
    }
}
