package com.tutorial.restful.api.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tutorial.restful.api.dto.RegisterUserRequest;
import com.tutorial.restful.api.dto.UserResponse;
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
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        // setiap di jalankan data yang ada di table user selalu di hapus
        userRepository.deleteAll(); // void deleteAll() // Menghapus semua entitas yang dikelola oleh repositori.
    }

    @Test
    void testRegisterSuccess() throws Exception {
        RegisterUserRequest request = RegisterUserRequest.builder()
                .username("test")
                .password("rahasia")
                .name("Test")
                .build();

        mockMvc.perform(
                post("/api/users")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isOk()
        ).andDo(new ResultHandler() {
            @Override
            public void handle(MvcResult result) throws Exception {
                WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<String>>() {
                });

                Assertions.assertNotNull(response);
                Assertions.assertEquals("OK", response.getData());
            }
        });

        /**
         * Response Body = {"data":"OK","errors":null}
         * result query:
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
         *         count(*)
         *     from
         *         users u1_0
         *     where
         *         u1_0.username=?
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
         */

    }

    @Test
    void testRegisterBadRequest() throws Exception {
        RegisterUserRequest request = RegisterUserRequest.builder()
                .username("")
                .password("")
                .name("")
                .build();

        mockMvc.perform(
                post("/api/users")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isBadRequest()
        ).andDo(new ResultHandler() {
            @Override
            public void handle(MvcResult result) throws Exception {
                WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<String>>() {
                });

                Assertions.assertNotNull(response.getErrors());

            }
        });

        /**
         * Response Body = {"data":null,"errors":"name: must not be blank, password: must not be blank, username: must not be blank"}
         * result query:
         * Hibernate:
         *     select
         *         u1_0.username,
         *         u1_0.name,
         *         u1_0.password,
         *         u1_0.token,
         *         u1_0.token_expired_at
         *     from
         *         users u1_0
         */

    }

    @Test
    void testRegisterDuplicate() throws Exception {

        User user = new User();
        user.setUsername("test");
        user.setPassword(BCrypt.hashpw("rahasia", BCrypt.gensalt()));
        user.setName("Test");
        userRepository.save(user);

        RegisterUserRequest request = RegisterUserRequest.builder()
                .username("test")
                .password("rahasia")
                .name("Test")
                .build();

        mockMvc.perform(
                post("/api/users")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isBadRequest()
        ).andDo(new ResultHandler() {
            @Override
            public void handle(MvcResult result) throws Exception {
                WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<String>>() {
                });

                Assertions.assertNotNull(response.getErrors());

            }
        });

        /**
         * Response Body = {"data":null,"errors":"Username already registered"}
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
         *         count(*)
         *     from
         *         users u1_0
         *     where
         *         u1_0.username=?
         */

    }

    @Test
    void getUserUnauthorized() throws Exception {

        mockMvc.perform(
                get("/api/users/current")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", "notfound")
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            Assertions.assertNotNull(response.getErrors());

        });

        /**
         * Response Body = {"data":null,"errors":"Unauthorized"}
         * result query:
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
         */

    }

    @Test
    void getUserUnauthorizedTokenNotSend() throws Exception {

        mockMvc.perform(
                get("/api/users/current")
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            Assertions.assertNotNull(response.getErrors());

        });

        /**
         * Response Body = {"data":null,"errors":"Unauthorized"}
         * result query:
         * Hibernate:
         *     select
         *         u1_0.username,
         *         u1_0.name,
         *         u1_0.password,
         *         u1_0.token,
         *         u1_0.token_expired_at
         *     from
         *         users u1_0
         */

    }

    @Test
    void getUserSuccess() throws Exception {

        User user = new User();
        user.setUsername("test");
        user.setName("Test");
        user.setPassword(BCrypt.hashpw("rahasia", BCrypt.gensalt()));
        user.setToken("test");
        user.setTokenExpiredAt(System.currentTimeMillis() + 10000000000L);
        userRepository.save(user);

        mockMvc.perform(
                get("/api/users/current")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", "test")
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<UserResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertNull(response.getErrors());
            assertEquals("test", response.getData().getUsername());
            assertEquals("Test", response.getData().getName());
        });

        /**
         * Response Body = {"data":{"username":"test","name":"Test"},"errors":null}
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
         */

    }

    @Test
    void getUserTokenExpired() throws Exception {

        User user = new User();
        user.setUsername("test");
        user.setPassword(BCrypt.hashpw("rahasia", BCrypt.gensalt()));
        user.setName("Test");
        user.setToken("test");
        user.setTokenExpiredAt(System.currentTimeMillis() - 10000000);
        userRepository.save(user);

        mockMvc.perform(
                get("/api/users/current")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", "test")
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertNotNull(response.getErrors());
        });

        /**
         * Response Body = {"data":null,"errors":"Unauthorized"}
         * result query:
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
         */
    }

}