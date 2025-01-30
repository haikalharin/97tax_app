package com.repnox.nineseventax.features.auth;

import com.repnox.nineseventax.exceptions.AccessDeniedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import javax.servlet.http.HttpSession;

@Component
public class AuthService {

    @Autowired
    private Authenticator authenticator;

    public static final String AUTH_SESSION = "AUTH_SESSION";

    public AuthSession getAuthSession(HttpSession session) {
        Object value = session.getAttribute(AUTH_SESSION);
        if (value != null) {
            return (AuthSession) value;
        } else {
            return null;
        }
    }

    public AuthSession login(HttpSession session, String username, String password) {
        AuthUser user = authenticator.authenticate(username, password);
        if (user != null) {
            AuthSession authSession = new AuthSession();
            authSession.setAuthUser(user);
            session.setAttribute(AUTH_SESSION, authSession);
            return authSession;
        } else {
            throw new AccessDeniedException();
        }
    }

    public void logout(HttpSession session) {
        session.setAttribute(AUTH_SESSION, null);
    }

    public void requireRole(HttpSession session, AuthRole role) {
        AuthSession authSession = getAuthSession(session);
        if (authSession != null && authSession.getAuthUser() != null) {
            if (authSession.getAuthUser().getRoles().contains(role)) {
                return;
            }
        }

        throw new AccessDeniedException();
    }

    public void requireRoles(HttpSession session, List<AuthRole> role) {
        AuthSession authSession = getAuthSession(session);
        if (authSession != null && authSession.getAuthUser() != null) {
            if (authSession.getAuthUser().getRoles().contains(role.get(0))
                    || authSession.getAuthUser().getRoles().contains(role.get(1))) {
                return;
            }
        }
        throw new AccessDeniedException();
    }

    public void requireAdminRole(HttpSession session) {
        AuthSession authSession = getAuthSession(session);
        if (authSession != null && authSession.getAuthUser() != null) {
            if (authSession.getAuthUser().getRoles().contains(AuthRole.ADMIN)) {
                return;
            }
        }

        throw new AccessDeniedException();
    }

    public void requireAdminOrMailRoomUserRole(HttpSession session) {
        AuthSession authSession = getAuthSession(session);
        if (authSession != null && authSession.getAuthUser() != null) {
            if (authSession.getAuthUser().getRoles().contains(AuthRole.ADMIN)
                    || authSession.getAuthUser().getRoles().contains(AuthRole.MAILROOMUSER)) {
                return;
            }
        }

        throw new AccessDeniedException();
    }

    public boolean isAdminRole(HttpSession session) {
        AuthSession authSession = getAuthSession(session);
        if (authSession != null && authSession.getAuthUser() != null) {
            if (authSession.getAuthUser().getRoles().contains(AuthRole.ADMIN)) {
                return true;
            }
        }

        return false;
    }
}
