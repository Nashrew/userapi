package com.andrewn.userapi.controller;

import com.andrewn.userapi.model.jwt.JwtRequest;
import com.andrewn.userapi.model.jwt.JwtResponse;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class JwtAuthenticationControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testLogin_successful() {
        ResponseEntity<JwtResponse> response = testRestTemplate.postForEntity("http://localhost:" + port + "/auth/login",
                new JwtRequest("developer", "dev"),
                JwtResponse.class);

        Assertions.assertThat(HttpStatus.OK).isEqualTo(response.getStatusCode());
    }

    @Test
    public void testLogin_badCredentials() {
        ResponseEntity<JwtResponse> response = testRestTemplate.postForEntity("http://localhost:" + port + "/auth/login",
                new JwtRequest("developer", "nottherightpassword"),
                JwtResponse.class);

        Assertions.assertThat(HttpStatus.UNAUTHORIZED).isEqualTo(response.getStatusCode());
    }
}
