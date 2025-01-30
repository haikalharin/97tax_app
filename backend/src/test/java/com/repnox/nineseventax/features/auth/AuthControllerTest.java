package com.repnox.nineseventax.features.auth;

import com.repnox.nineseventax.BaseTest;
import com.repnox.nineseventax.features.mailroom.model.MailRoomUser;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Slf4j
public class AuthControllerTest extends BaseTest {

    @Test
    public void testLogin() {
        AuthUser validCredentials = AuthUser.builder()
                .username(ADMIN_USER).password(ADMIN_PASSWORD).build();
        ResponseEntity<UserRecord> responseValid =
                restTemplate.exchange(baseURI + "/auth/login", HttpMethod.POST, new HttpEntity<>(validCredentials), UserRecord.class);

        assertEquals(HttpStatus.OK, responseValid.getStatusCode());
        UserRecord user = responseValid.getBody();
        assertEquals(ADMIN_USER, user.getUsername());
        assertEquals("admin", user.getUserType());
    }

    @Test
    public void testLoginMailRoom() {
        AuthUser mailRoomCredentials = AuthUser.builder()
                .username(MAILROOM_USER).password(MAILROOM_PASSWORD).build();
        ResponseEntity<MailRoomUser> response =
                restTemplate.exchange(baseURI + "/auth/login", HttpMethod.POST, new HttpEntity<>(mailRoomCredentials), MailRoomUser.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        MailRoomUser user = response.getBody();
        assertEquals(MAILROOM_USER, user.getEmail());
        assertEquals("97tax Mail Room Test", user.getUserName());
    }

    @Test
    public void testInvalidCredentials() {
        AuthUser invalidCredentials = AuthUser.builder()
                .username(ADMIN_USER).password("wr0ngp@ssw0rd").build();
        ResponseEntity<UserRecord> responseInvalid =
                restTemplate.exchange(baseURI + "/auth/login", HttpMethod.POST, new HttpEntity<>(invalidCredentials), UserRecord.class);

        assertEquals(HttpStatus.UNAUTHORIZED, responseInvalid.getStatusCode());
    }

}
