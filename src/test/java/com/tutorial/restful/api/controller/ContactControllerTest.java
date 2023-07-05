package com.tutorial.restful.api.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tutorial.restful.api.dto.*;
import com.tutorial.restful.api.entity.Contact;
import com.tutorial.restful.api.entity.User;
import com.tutorial.restful.api.repository.ContactRepository;
import com.tutorial.restful.api.repository.UserRepository;
import com.tutorial.restful.api.security.BCrypt;
import lombok.extern.slf4j.Slf4j;
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

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.MockMvcBuilder.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
class ContactControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ContactRepository contactRepository;

    @Autowired
    ObjectMapper objectMapper;


    @BeforeEach
    void setUp() {
        // sebelum di test semua record data table akan di hapus
        contactRepository.deleteAll();
        userRepository.deleteAll();

        // sebelum unit test di jalankan alih alaih ini untuk login
        User user = new User();
        user.setUsername("budhi");
        user.setPassword(BCrypt.hashpw("rahasia", BCrypt.gensalt()));
        user.setName("Budhi");
        user.setToken("contact");
        user.setTokenExpiredAt(System.currentTimeMillis() + 1000000);
        userRepository.save(user);

    }

    @Test
    void testCreateContractBlank() throws Exception {

        CreateContactRequest request = new CreateContactRequest();
        request.setFirstName("");
        request.setLastName("");
        request.setEmail("sdfklsd");
        request.setPhone("");

        mockMvc.perform(
                post("/api/contacts")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("X-API-TOKEN", "contact")
        ).andExpectAll(
                status().isBadRequest()
        ).andDo(new ResultHandler() {
            @Override
            public void handle(MvcResult result) throws Exception {
                WebResponse<ContactResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                });

                Assertions.assertNotNull(response.getErrors());

            }
        });

        /**
         * Response Body = {"data":null,"errors":"email: must be a well-formed email address, firstName: must not be blank"}
         */

    }

    @Test
    void testCreateContractUnauthorized() throws Exception {

        CreateContactRequest request = new CreateContactRequest();
        request.setFirstName("");
        request.setLastName("");
        request.setEmail("sdfklsd");
        request.setPhone("");

        mockMvc.perform(
                post("/api/contacts")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(new ResultHandler() {
            @Override
            public void handle(MvcResult result) throws Exception {
                WebResponse<ContactResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                });

                Assertions.assertNotNull(response.getErrors());

            }
        });

        /**
         * Response Body = {"data":null,"errors":"Unauthorized"}
         */

    }

    @Test
    void testCreateContractSuccess() throws Exception {

        CreateContactRequest request = new CreateContactRequest();
        request.setFirstName("budhi oct");
        request.setLastName("octaviansyah");
        request.setEmail("budioct@contoh.com");
        request.setPhone("08999912222");

        mockMvc.perform(
                post("/api/contacts")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("X-API-TOKEN", "contact")
        ).andExpectAll(
                status().isOk()
        ).andDo(new ResultHandler() {
            @Override
            public void handle(MvcResult result) throws Exception {
                WebResponse<ContactResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                });

                Assertions.assertNull(response.getErrors());
                Assertions.assertNotNull(response.getData());

                Assertions.assertEquals(request.getFirstName(), response.getData().getFirstName());
                Assertions.assertEquals(request.getLastName(), response.getData().getLastName());
                Assertions.assertEquals(request.getEmail(), response.getData().getEmail());
                Assertions.assertEquals(request.getPhone(), response.getData().getPhone());

                assertTrue(contactRepository.existsById(response.getData().getId()));

            }
        });

        /**
         * Response Body = {"data":{"id":"91a6efb6-3c6f-4e4c-bafd-051b9410d1ce","firstName":"budhi oct","lastName":"octaviansyah","email":"budioct@contoh.com","phone":"08999912222"},"errors":null}
         */

    }

    @Test
    void testGetContactNotFound() throws Exception {

        mockMvc.perform(
                get("/api/contacts/notfound")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header("X-API-TOKEN", "contact")
        ).andExpectAll(
                status().isNotFound()
        ).andDo(result -> {
            WebResponse<ContactResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            Assertions.assertNotNull(response.getErrors());

        });

        /**
         * Response Body = {"data":null,"errors":"Contact not found"}
         */

    }

    @Test
    void testGetContactUnauthorized() throws Exception {

        // save contanct mula mula untuk sudah login dengan id user budhi
        Contact contact = new Contact();
        contact.setId(UUID.randomUUID().toString());

        mockMvc.perform(
                get("/api/contacts/" + contact.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
//                        .header("X-API-TOKEN", "contact")
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
            WebResponse<ContactResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            Assertions.assertNotNull(response.getErrors());

        });

        /**
         * Response Body = {"data":null,"errors":"Unauthorized"}
         */

    }

    @Test
    void testGetContactSuccess() throws Exception {

        // select user id
        User user = userRepository.findById("budhi").orElseThrow();

        // save contanct mula mula untuk sudah login dengan id user budhi
        Contact contact = new Contact();
        contact.setId(UUID.randomUUID().toString());
        contact.setFirstName("Budhi");
        contact.setLastName("Octaviansyah");
        contact.setEmail("budioct@example.com");
        contact.setPhone("08999912222");
        contact.setUser(user); // relasi
        contactRepository.save(contact);

        mockMvc.perform(
                get("/api/contacts/" + contact.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header("X-API-TOKEN", "contact")
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<ContactResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            Assertions.assertNull(response.getErrors());

            Assertions.assertEquals(contact.getId(), response.getData().getId());
            Assertions.assertEquals(contact.getFirstName(), response.getData().getFirstName());
            Assertions.assertEquals(contact.getLastName(), response.getData().getLastName());
            Assertions.assertEquals(contact.getEmail(), response.getData().getEmail());
            Assertions.assertEquals(contact.getPhone(), response.getData().getPhone());

        });

        /**
         * Response Body = {"data":{"id":"e34232bf-532a-4a06-a834-23e0ccad7e66","firstName":"Budhi","lastName":"Octaviansyah","email":"budioct@example.com","phone":"08999912222"},"errors":null}
         */

    }

    @Test
    void testUpdateContractBlank() throws Exception {

        UpdateContactRequest request = new UpdateContactRequest();
        request.setId(UUID.randomUUID().toString());
        request.setFirstName("");
        request.setLastName("");
        request.setEmail("sdfklsd");
        request.setPhone("");

        mockMvc.perform(
                put("/api/contacts/" + request.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("X-API-TOKEN", "contact")
        ).andExpectAll(
                status().isBadRequest()
        ).andDo(new ResultHandler() {
            @Override
            public void handle(MvcResult result) throws Exception {
                WebResponse<ContactResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                });

                Assertions.assertNotNull(response.getErrors());

            }
        });

        /**
         * Response Body = {"data":null,"errors":"email: must be a well-formed email address, firstName: must not be blank"}
         */

    }

    @Test
    void testUpdateContractUnauthorized() throws Exception {

        UpdateContactRequest request = new UpdateContactRequest();
        request.setId(UUID.randomUUID().toString());
        request.setFirstName("");
        request.setLastName("");
        request.setEmail("sdfklsd");
        request.setPhone("");

        mockMvc.perform(
                put("/api/contacts/" + request.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(new ResultHandler() {
            @Override
            public void handle(MvcResult result) throws Exception {
                WebResponse<ContactResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                });

                Assertions.assertNotNull(response.getErrors());

            }
        });

        /**
         * Response Body = {"data":null,"errors":"Unauthorized"}
         */

    }

    @Test
    void testUpdateContactSuccess() throws Exception {

        // select user id
        User user = userRepository.findById("budhi").orElseThrow();

        // save contanct mula mula untuk sudah login dengan id user budhi
        Contact contact = new Contact();
        contact.setId(UUID.randomUUID().toString());
        contact.setUser(user); // relasi
        contact.setFirstName("Budhi");
        contact.setLastName("Octaviansyah");
        contact.setEmail("budioct@example.com");
        contact.setPhone("08999912222");
        contactRepository.save(contact);

        // update contact yang sebelumnya
        UpdateContactRequest request = new UpdateContactRequest();
        request.setFirstName("mali");
        request.setLastName("madun");
        request.setEmail("malimadun@example.com");
        request.setPhone("1111155555");

        mockMvc.perform(
                put("/api/contacts/" + contact.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header("X-API-TOKEN", "contact")
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<ContactResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            Assertions.assertNull(response.getErrors());

            Assertions.assertEquals(request.getFirstName(), response.getData().getFirstName());
            Assertions.assertEquals(request.getLastName(), response.getData().getLastName());
            Assertions.assertEquals(request.getEmail(), response.getData().getEmail());
            Assertions.assertEquals(request.getPhone(), response.getData().getPhone());

            Assertions.assertTrue(contactRepository.existsById(response.getData().getId()));

            log.info("Before id= {}", contact.getId());
            log.info("After id: {}", response.getData().getId());

        });

        /**
         * // before update
         * Response Body = {"data":{"id":"b35f1ec8-a067-43a5-b62d-33398bbaecaa","firstName":"Budhi","lastName":"Octaviansyah","email":"budioct@example.com","phone":"08999912222"},"errors":null}
         *
         * // after update
         * Response Body = {"data":{"id":"b35f1ec8-a067-43a5-b62d-33398bbaecaa","firstName":"mali","lastName":"madun","email":"malimadun@example.com","phone":"1111155555"},"errors":null}
         */

    }

    @Test
    void testDeleteContractBlank() throws Exception {

        mockMvc.perform(
                delete("/api/contacts/asalasalan")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", "contact")
        ).andExpectAll(
                status().isNotFound()
        ).andDo(new ResultHandler() {
            @Override
            public void handle(MvcResult result) throws Exception {
                WebResponse<ContactResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                });

                Assertions.assertNotNull(response.getErrors());

            }
        });

        /**
         * Response Body = {"data":null,"errors":"Contact not found"}
         */

    }

    @Test
    void testDeleteContractUnauthorized() throws Exception {

        mockMvc.perform(
                delete("/api/contacts/asalasalan")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(new ResultHandler() {
            @Override
            public void handle(MvcResult result) throws Exception {
                WebResponse<ContactResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                });

                Assertions.assertNotNull(response.getErrors());

            }
        });

        /**
         * Response Body = {"data":null,"errors":"Unauthorized"}
         */

    }

    @Test
    void testDeleteContactSuccess() throws Exception {

        // select user id
        User user = userRepository.findById("budhi").orElseThrow();

        // save contanct mula mula untuk sudah login dengan id user budhi
        Contact contact = new Contact();
        contact.setId(UUID.randomUUID().toString());
        contact.setUser(user); // relasi
        contact.setFirstName("Budhi");
        contact.setLastName("Octaviansyah");
        contact.setEmail("budioct@example.com");
        contact.setPhone("08999912222");
        contactRepository.save(contact);

        mockMvc.perform(
                delete("/api/contacts/" + contact.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header("X-API-TOKEN", "contact")
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            Assertions.assertNull(response.getErrors());
            Assertions.assertEquals("OK", response.getData());

        });

        /**
         * Response Body = {"data":"OK","errors":null}
         */

    }

    @Test
    void testSearchContactUnauthorized() throws Exception {

        mockMvc.perform(
                get("/api/contacts")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(new ResultHandler() {
            @Override
            public void handle(MvcResult result) throws Exception {
                WebResponse<List<ContactResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                });

                Assertions.assertNotNull(response.getErrors());

            }
        });

        /**
         * Response Body = {"data":null,"errors":"Unauthorized","paging":null}
         */

    }

    @Test
    void testSearchContactNotFound() throws Exception {

        mockMvc.perform(
                get("/api/contacts")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", "contact")
        ).andExpectAll(
                status().isOk()
        ).andDo(new ResultHandler() {
            @Override
            public void handle(MvcResult result) throws Exception {
                WebResponse<List<ContactResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                });

                Assertions.assertNull(response.getErrors());

                Assertions.assertEquals(0, response.getData().size());
                Assertions.assertEquals(0, response.getPaging().getCurrentPage());
                Assertions.assertEquals(0, response.getPaging().getTotalPage());
                Assertions.assertEquals(10, response.getPaging().getSize());

            }
        });

        /**
         * Response Body = {"data":[],"errors":null,"paging":{"currentPage":0,"totalPage":0,"size":10}}
         */

    }

    @Test
    void testSearchContactSuccess() throws Exception {

        // select user id, menandakan jika user sudah login dan mendapatkan token
        User user = userRepository.findById("budhi").orElseThrow();

        // mula mula kita sediakan data untuk di cari (search)
        for (int i = 0; i < 100; i++) {
            Contact contact = new Contact();
            contact.setId(UUID.randomUUID().toString());
            contact.setUser(user); // set user relasi
            contact.setFirstName("Budhi" + i); // query param "name"
            contact.setLastName("Octaviansyah"); // query param "name"
            contact.setEmail("budhioct@example.com"); // query param "email"
            contact.setPhone("08999912222"); // query param "phone"
            contactRepository.save(contact);
        }

        mockMvc.perform(
                get("/api/contacts")
                        .queryParam("name", "Budhi")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", "contact")
        ).andExpectAll(
                status().isOk()
        ).andDo(new ResultHandler() {
            @Override
            public void handle(MvcResult result) throws Exception {
                WebResponse<List<ContactResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                });

                Assertions.assertNull(response.getErrors());

                Assertions.assertEquals(10, response.getData().size()); // data ada 10
                Assertions.assertEquals(0, response.getPaging().getCurrentPage()); // sekarang halaman 1 (index di mulai dari 0)
                Assertions.assertEquals(10, response.getPaging().getTotalPage()); // total halaman 10 dari 100 data
                Assertions.assertEquals(10, response.getPaging().getSize()); // data setiap halaman ada 10 data

            }
        });

        /**
         * Response Body = {"data":[{"id":"026a85e6-534f-4ff1-bbc0-c301e0fe6391","firstName":"Budhi11","lastName":"Octaviansyah","email":"budhioct@example.com","phone":"08999912222"},{"id":"02e6ddf5-7029-4c7f-8bc4-edbf5dbe4032","firstName":"Budhi56","lastName":"Octaviansyah","email":"budhioct@example.com","phone":"08999912222"}],"errors":null,"paging":{"currentPage":0,"totalPage":10,"size":10}}
         */

        mockMvc.perform(
                get("/api/contacts")
                        .queryParam("name", "Octaviansyah")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", "contact")
        ).andExpectAll(
                status().isOk()
        ).andDo(new ResultHandler() {
            @Override
            public void handle(MvcResult result) throws Exception {
                WebResponse<List<ContactResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                });

                Assertions.assertNull(response.getErrors());

                Assertions.assertEquals(10, response.getData().size()); // data ada 10
                Assertions.assertEquals(0, response.getPaging().getCurrentPage()); // sekarang halaman 1 (index di mulai dari 0)
                Assertions.assertEquals(10, response.getPaging().getTotalPage()); // total halaman 10 dari 100 data
                Assertions.assertEquals(10, response.getPaging().getSize()); // data setiap halaman ada 10 data

            }
        });

        /**
         * Response Body = {"data":[],"errors":null,"paging":{"currentPage":0,"totalPage":10,"size":10}}
         */

        mockMvc.perform(
                get("/api/contacts")
                        .queryParam("email", "@example.com")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", "contact")
        ).andExpectAll(
                status().isOk()
        ).andDo(new ResultHandler() {
            @Override
            public void handle(MvcResult result) throws Exception {
                WebResponse<List<ContactResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                });

                Assertions.assertNull(response.getErrors());

                Assertions.assertEquals(10, response.getData().size()); // data ada 10
                Assertions.assertEquals(0, response.getPaging().getCurrentPage()); // sekarang halaman 1 (index di mulai dari 0)
                Assertions.assertEquals(10, response.getPaging().getTotalPage()); // total halaman 10 dari 100 data
                Assertions.assertEquals(10, response.getPaging().getSize()); // data setiap halaman ada 10 data

            }
        });

        /**
         * Response Body = {"data":[],"errors":null,"paging":{"currentPage":0,"totalPage":10,"size":10}}
         */

        mockMvc.perform(
                get("/api/contacts")
                        .queryParam("phone", "12222")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", "contact")
        ).andExpectAll(
                status().isOk()
        ).andDo(new ResultHandler() {
            @Override
            public void handle(MvcResult result) throws Exception {
                WebResponse<List<ContactResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                });

                Assertions.assertNull(response.getErrors());

                Assertions.assertEquals(10, response.getData().size()); // data ada 10
                Assertions.assertEquals(0, response.getPaging().getCurrentPage()); // sekarang halaman 1 (index di mulai dari 0)
                Assertions.assertEquals(10, response.getPaging().getTotalPage()); // total halaman 10 dari 100 data
                Assertions.assertEquals(10, response.getPaging().getSize()); // data setiap halaman ada 10 data

            }
        });

        /**
         * Response Body = {"data":[],"errors":null,"paging":{"currentPage":0,"totalPage":10,"size":10}}
         */

        mockMvc.perform(
                get("/api/contacts")
                        .queryParam("phone", "12222")
                        .queryParam("page", "1000")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", "contact")
        ).andExpectAll(
                status().isOk()
        ).andDo(new ResultHandler() {
            @Override
            public void handle(MvcResult result) throws Exception {
                WebResponse<List<ContactResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                });

                Assertions.assertNull(response.getErrors());

                Assertions.assertEquals(0, response.getData().size()); // data ada 10
                Assertions.assertEquals(1000, response.getPaging().getCurrentPage()); // sekarang halaman 1000 (index di mulai dari 0)
                Assertions.assertEquals(10, response.getPaging().getTotalPage()); // total halaman 10 dari 100 data
                Assertions.assertEquals(10, response.getPaging().getSize()); // data setiap halaman ada 10 data

            }
        });

        /**
         * Response Body = {"data":[],"errors":null,"paging":{"currentPage":1000,"totalPage":10,"size":10}}
         */

    }

}