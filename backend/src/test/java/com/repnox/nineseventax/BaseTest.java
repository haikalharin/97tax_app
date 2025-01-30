package com.repnox.nineseventax;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.repnox.nineseventax.features.auth.AuthUser;
import com.repnox.nineseventax.features.ecommerce.CentinelFacade;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.reactive.server.WebTestClient;

import javax.annotation.PostConstruct;

@Slf4j
@Getter
public class BaseTest {

    public static final String TEST_RESOURCES = "src/test/resources";

    protected final String ADMIN_USER = "admin";
    protected final String ADMIN_PASSWORD = "3F91#uk1O528";

    protected final String MAILROOM_USER = "mail.room.97tax@mailinator.com";
    protected final String MAILROOM_PASSWORD = "password";

    @LocalServerPort
    private int port;
    protected String baseURI;

    @Autowired
    protected TestRestTemplate restTemplate;

    @Autowired
    protected WebTestClient webClient;

    @Autowired
    protected ObjectMapper mapper;

    @Autowired
    private CentinelFacade centinelFacade;

    @PostConstruct
    private void init() {
        this.baseURI = String.format("http://localhost:%s/api", port);
    }

    public String loginAdmin() {
        return doLogin(ADMIN_USER, ADMIN_PASSWORD);
    }

    public String loginMailRoom() {
        return doLogin(MAILROOM_USER, MAILROOM_PASSWORD);
    }

    private String doLogin(String username, String password) {
        AuthUser mailRoomCredentials = AuthUser.builder()
                .username(username).password(password).build();
        ResponseEntity<Void> response =
                restTemplate.exchange(baseURI + "/auth/login", HttpMethod.POST, new HttpEntity<>(mailRoomCredentials), Void.class);
        String sessionCookie = response.getHeaders().get("Set-Cookie").get(0);
        return sessionCookie;
    }

}
