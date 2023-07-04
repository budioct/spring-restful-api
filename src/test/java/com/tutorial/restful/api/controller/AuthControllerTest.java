package com.tutorial.restful.api.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tutorial.restful.api.dto.LoginUserRequest;
import com.tutorial.restful.api.dto.TokenResponse;
import com.tutorial.restful.api.dto.WebResponse;
import com.tutorial.restful.api.entity.User;
import com.tutorial.restful.api.repository.UserRepository;
import com.tutorial.restful.api.security.BCrypt;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultHandler;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.MockMvcBuilder.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    void loginFailedUserNotFound() throws Exception {

        LoginUserRequest request = new LoginUserRequest();
        request.setUsername("test");
        request.setPassword("test");

        mockMvc.perform(
                post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(new ResultHandler() {
            @Override
            public void handle(MvcResult result) throws Exception {
                WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<String>>() {
                });

                Assertions.assertNotNull(response.getErrors());
            }
        });

        /**
         * response body = {"data":null,"errors":"Username or password wrong"}
         * query result:
         * Hibernate:
         *     select
         *         u1_0.username,
         *         u1_0.name,
         *         u1_0.password,
         *         u1_0.token,
         *         u1_0.token_expired_at
         *     from
         *         users u1_0
         *     where
         *         u1_0.username=?
         */

    }

    @Test
    void loginFailedWrongPassword() throws Exception {

        User user = new User();
        user.setName("Test");
        user.setUsername("test");
        user.setPassword(BCrypt.hashpw("test", BCrypt.gensalt()));
        userRepository.save(user);

        LoginUserRequest request = new LoginUserRequest();
        request.setUsername("test");
        request.setPassword("salah");

        mockMvc.perform(
                post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(new ResultHandler() {
            @Override
            public void handle(MvcResult result) throws Exception {
                WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<String>>() {
                });

                Assertions.assertNotNull(response.getErrors());
            }
        });

        /**
         * response body = {"data":null,"errors":"Username or password wrong"}
         * query result:
         * Hibernate:
         *     select
         *         u1_0.username,
         *         u1_0.name,
         *         u1_0.password,
         *         u1_0.token,
         *         u1_0.token_expired_at
         *     from
         *         users u1_0
         * Hibernate:
         *     delete
         *     from
         *         users
         *     where
         *         username=?
         * Hibernate:
         *     select
         *         u1_0.username,
         *         u1_0.name,
         *         u1_0.password,
         *         u1_0.token,
         *         u1_0.token_expired_at
         *     from
         *         users u1_0
         *     where
         *         u1_0.username=?
         * Hibernate:
         *     insert
         *     into
         *         users
         *         (name, password, token, token_expired_at, username)
         *     values
         *         (?, ?, ?, ?, ?)
         * Hibernate:
         *     select
         *         u1_0.username,
         *         u1_0.name,
         *         u1_0.password,
         *         u1_0.token,
         *         u1_0.token_expired_at
         *     from
         *         users u1_0
         *     where
         *         u1_0.username=?
         */

    }

    @Test
    void loginSuccess() throws Exception {

        User user = new User();
        user.setName("Test");
        user.setUsername("test");
        user.setPassword(BCrypt.hashpw("test", BCrypt.gensalt()));
        userRepository.save(user);

        LoginUserRequest request = new LoginUserRequest();
        request.setUsername("test");
        request.setPassword("test");

        mockMvc.perform(
                post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isOk()
        ).andDo(new ResultHandler() {
            @Override
            public void handle(MvcResult result) throws Exception {
                WebResponse<TokenResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<TokenResponse>>() {
                });

                Assertions.assertNull(response.getErrors());
                Assertions.assertNotNull(response.getData().getToken());
                Assertions.assertNotNull(response.getData().getExpiredAt());

                User userDB = userRepository.findById("test").orElse(null);
                Assertions.assertNotNull(userDB);
                Assertions.assertEquals(userDB.getToken(), response.getData().getToken());
                Assertions.assertEquals(userDB.getTokenExpiredAt(), response.getData().getExpiredAt());


            }
        });

        /**
         * response body = {"data":{"token":"2dd32768-9bd9-45ed-a60e-c5eb0e6521bd","expiredAt":1688437213465},"errors":null}
         * query result:
         * Hibernate:
         *     select
         *         u1_0.username,
         *         u1_0.name,
         *         u1_0.password,
         *         u1_0.token,
         *         u1_0.token_expired_at
         *     from
         *         users u1_0
         * Hibernate:
         *     delete
         *     from
         *         users
         *     where
         *         username=?
         * Hibernate:
         *     select
         *         u1_0.username,
         *         u1_0.name,
         *         u1_0.password,
         *         u1_0.token,
         *         u1_0.token_expired_at
         *     from
         *         users u1_0
         *     where
         *         u1_0.username=?
         * Hibernate:
         *     insert
         *     into
         *         users
         *         (name, password, token, token_expired_at, username)
         *     values
         *         (?, ?, ?, ?, ?)
         * Hibernate:
         *     select
         *         u1_0.username,
         *         u1_0.name,
         *         u1_0.password,
         *         u1_0.token,
         *         u1_0.token_expired_at
         *     from
         *         users u1_0
         *     where
         *         u1_0.username=?
         * Hibernate:
         *     update
         *         users
         *     set
         *         name=?,
         *         password=?,
         *         token=?,
         *         token_expired_at=?
         *     where
         *         username=?
         * Hibernate:
         *     select
         *         u1_0.username,
         *         u1_0.name,
         *         u1_0.password,
         *         u1_0.token,
         *         u1_0.token_expired_at
         *     from
         *         users u1_0
         *     where
         *         u1_0.username=?
         */

    }

    @Test
    void testLogoutFailed() throws Exception {

        mockMvc.perform(
                delete("/api/auth/logout")
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNotNull(response.getErrors());
        });

        /**
         * Response Body= {"data":null,"errors":"Unauthorized"}
         * query result:
         * Hibernate:
         *     select
         *         u1_0.username,
         *         u1_0.name,
         *         u1_0.password,
         *         u1_0.token,
         *         u1_0.token_expired_at
         *     from
         *         users u1_0
         * Hibernate:
         *     delete
         *     from
         *         users
         *     where
         *         username=?
         */
    }

    @Test
    void logoutSuccess() throws Exception {

        User user = new User();
        user.setUsername("test");
        user.setName("budhi");
        user.setPassword(BCrypt.hashpw("rahasia", BCrypt.gensalt()));
        user.setToken("test");
        user.setTokenExpiredAt(System.currentTimeMillis() + 10000000L);
        userRepository.save(user);

        mockMvc.perform(
                delete("/api/auth/logout")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", "test")
                        //.content(objectMapper.writeValueAsString(user))
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            Assertions.assertNull(response.getErrors());
            Assertions.assertEquals("OK", response.getData());

            User userDb = userRepository.findById("test").orElse(null);
            Assertions.assertNotNull(userDb);
            Assertions.assertNull(userDb.getTokenExpiredAt());
            Assertions.assertNull(userDb.getToken());
        });

        /**
         * Response Body= {"data":"OK","errors":null}
         * query result:
         * Hibernate:
         *     select
         *         u1_0.username,
         *         u1_0.name,
         *         u1_0.password,
         *         u1_0.token,
         *         u1_0.token_expired_at
         *     from
         *         users u1_0
         * Hibernate:
         *     delete
         *     from
         *         users
         *     where
         *         username=?
         * Hibernate:
         *     select
         *         u1_0.username,
         *         u1_0.name,
         *         u1_0.password,
         *         u1_0.token,
         *         u1_0.token_expired_at
         *     from
         *         users u1_0
         *     where
         *         u1_0.username=?
         * Hibernate:
         *     insert
         *     into
         *         users
         *         (name, password, token, token_expired_at, username)
         *     values
         *         (?, ?, ?, ?, ?)
         * Hibernate:
         *     select
         *         u1_0.username,
         *         u1_0.name,
         *         u1_0.password,
         *         u1_0.token,
         *         u1_0.token_expired_at
         *     from
         *         users u1_0
         *     where
         *         u1_0.token=? limit ?
         * Hibernate:
         *     update
         *         users
         *     set
         *         name=?,
         *         password=?,
         *         token=?,
         *         token_expired_at=?
         *     where
         *         username=?
         * Hibernate:
         *     select
         *         u1_0.username,
         *         u1_0.name,
         *         u1_0.password,
         *         u1_0.token,
         *         u1_0.token_expired_at
         *     from
         *         users u1_0
         *     where
         *         u1_0.username=?
         */
    }



}