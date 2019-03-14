package com.neperix.osiris.security.jwt;

import static org.hamcrest.Matchers.equalToIgnoringCase;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;

import org.hamcrest.FeatureMatcher;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.neperix.osiris.security.SecurityService;

@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@SpringBootTest(classes = JwtAuthenticationControllerTest.TestConfig.class)
public class JwtAuthenticationControllerTest {

    private static final LocalDateTime ONE_HOUR_AGO = LocalDateTime.now().minusHours(1L);
    private static final LocalDateTime ONE_HOUR_FROM_NOW = LocalDateTime.now().plusHours(1L);

    @Autowired
    private MockMvc mvc;

    @Test
    public void itShouldReturnValidJwtTokenForSuccessfullyAuthenticatedUser() throws Exception {

        mvc.perform(post("/auth").content("{\"username\":\"ragnar.lothbrok\", \"password\":\"password\"}")
            .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content()
                    .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.token", not(nullValue())))
                .andExpect(jsonPath("$.token", matchesUsername("ragnar.lothbrok")));
    }

    @Test
    public void itShouldReturn4xxWhenForUnsuccessfullAuthentication() throws Exception {

        mvc.perform(post("/auth")
                .content("{\"username\":\"echbert\", \"password\":\"password\"}")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void itShouldRefreshJwtTokenWhenTheOldOneIsSentInHeader() throws Exception {

        Algorithm alg = Algorithm.HMAC256("osiris");
        String oldToken = JWT.create()
                .withIssuer("osiris")
                .withClaim("username", "lagertha")
                .withExpiresAt(Date.from(ONE_HOUR_AGO.atZone(ZoneId.systemDefault()).toInstant()))
                .sign(alg);

        mvc.perform(get("/refresh").header("Authorization", "Bearer " + oldToken))
                .andExpect(status().isOk())
                .andExpect(content()
                    .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.token", matchesUsername("lagertha")));
    }

    @Test
    public void itShouldFailToRefreshTokenWhenTheOldOneIsNotSent() throws Exception {
        mvc.perform(get("/refresh"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void itShouldFailToRefreshTokenIfUsernameIsNotValidAnymore() throws Exception {
        Algorithm alg = Algorithm.HMAC256("osiris");
        String oldToken = JWT.create()
                .withIssuer("osiris")
                .withClaim("username", "rollo")
                .withExpiresAt(Date.from(ONE_HOUR_AGO.atZone(ZoneId.systemDefault()).toInstant()))
                .sign(alg);

        mvc.perform(get("/refresh").header("Authorization", oldToken))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void itShouldFailToRefreshTokenIfOldExpirationDateIsNotSet() throws Exception {
        Algorithm alg = Algorithm.HMAC256("osiris");
        String oldToken = JWT.create()
                .withIssuer("osiris")
                .withClaim("username", "rollo")
                .sign(alg);

        mvc.perform(get("/refresh").header("Authorization", oldToken))
                .andExpect(status().is4xxClientError())
        .andExpect(jsonPath("$.message", equalToIgnoringCase("Unable to decode token: " + oldToken)));
    }

    private FeatureMatcher<String, String> matchesUsername(String username) {
        return new FeatureMatcher<String, String>(equalToIgnoringCase(username), "username", "username") {
            @Override
            protected String featureValueOf(String token) {
                DecodedJWT decoded = JWT.decode(token);
                return decoded.getClaim("username").asString();
            }
        };
    }


    @EnableJwt
    static final class TestConfig {

        @Bean
        SecurityService securityService() {
            return new MockSecurityService(Arrays.asList("ragnar.lothbrok", "lagertha"));
        }
    }
}
