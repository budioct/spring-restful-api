package com.tutorial.restful.api.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tutorial.restful.api.dto.AddressResponse;
import com.tutorial.restful.api.dto.CreateAddressRequest;
import com.tutorial.restful.api.dto.UpdateAddressRequest;
import com.tutorial.restful.api.dto.WebResponse;
import com.tutorial.restful.api.entity.Address;
import com.tutorial.restful.api.entity.Contact;
import com.tutorial.restful.api.entity.User;
import com.tutorial.restful.api.repository.AddressRepository;
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

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

import static org.springframework.test.web.servlet.MockMvcBuilder.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
class AddressControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ContactRepository contactRepository;

    @Autowired
    AddressRepository addressRepository;

    @Autowired
    ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {

        // sebelum di test semua record data table akan di hapus
        addressRepository.deleteAll();
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

        // save contanct mula mula untuk sudah login dengan id user budhi
        Contact contact = new Contact();
        contact.setId("contactId");
        contact.setFirstName("Budhi");
        contact.setLastName("Octaviansyah");
        contact.setEmail("budioct@example.com");
        contact.setPhone("08999912222");
        contact.setUser(user); // relasi
        contactRepository.save(contact);

    }

    @Test
    void testCreateAddressBadRequest() throws Exception {

        CreateAddressRequest request = new CreateAddressRequest();
        request.setCountry(""); // filed country yang di set not blank

        mockMvc.perform(
                post("/api/contacts/contactId/addresses")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("X-API-TOKEN", "contact")
        ).andExpectAll(
                status().isBadRequest()
        ).andDo(result -> {
            WebResponse<AddressResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            Assertions.assertNotNull(response.getErrors());

        });

        /**
         * Response Body = {"data":null,"errors":"country: must not be blank","paging":null}
         */
    }

    @Test
    void testCreateAddressUnauthorized() throws Exception {

        CreateAddressRequest request = new CreateAddressRequest();
        request.setCountry(""); // filed country yang di set not blank

        mockMvc.perform(
                post("/api/contacts/contactId/addresses")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                //.header("X-API-TOKEN", "contact")
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
            WebResponse<AddressResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            Assertions.assertNotNull(response.getErrors());

        });

        /**
         * Response Body = {"data":null,"errors":"Unauthorized","paging":null}
         */
    }

    @Test
    void testCreateAddressSuccess() throws Exception {

        CreateAddressRequest request = new CreateAddressRequest();
        request.setStreet("jl.manju");
        request.setCity("prembun");
        request.setProvince("jawa");
        request.setCountry("indonesia");
        request.setPostalCode("15155");

        mockMvc.perform(
                post("/api/contacts/contactId/addresses")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("X-API-TOKEN", "contact")
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<AddressResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            Assertions.assertNull(response.getErrors());

            Assertions.assertEquals(request.getStreet(), response.getData().getStreet());
            Assertions.assertEquals(request.getCity(), response.getData().getCity());
            Assertions.assertEquals(request.getProvince(), response.getData().getProvince());
            Assertions.assertEquals(request.getCountry(), response.getData().getCountry());
            Assertions.assertEquals(request.getPostalCode(), response.getData().getPostalCode());

            Assertions.assertTrue(addressRepository.existsById(response.getData().getId()));

        });

        /**
         * Response Body = {"street":"jl.manju","city":"prembun","province":"jawa","country":"indonesia","postalCode":"15155"}
         */
    }

    @Test
    void testGetAddressNotFound() throws Exception {

        mockMvc.perform(
                get("/api/contacts/asalasalan/address/asalasalan")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header("X-API-TOKEN", "contact")
        ).andExpectAll(
                status().isNotFound()
        ).andDo(result -> {
            WebResponse<AddressResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            Assertions.assertNotNull(response.getErrors());

        });

        /**
         * Response Body = {"data":null,"errors":"Contact is not found","paging":null}
         */
    }

    @Test
    void testGetAddressUnauthorized() throws Exception {

        CreateAddressRequest request = new CreateAddressRequest();
        request.setCountry(""); // filed country yang di set not blank

        mockMvc.perform(
                get("/api/contacts/asalasalan/address/asalasalan")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request))
                //.header("X-API-TOKEN", "contact")
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
            WebResponse<AddressResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            Assertions.assertNotNull(response.getErrors());

        });

        /**
         * Response Body = {"data":null,"errors":"Unauthorized","paging":null}
         */
    }

    @Test
    void testGetAddressSuccess() throws Exception {

        // select id contact
        Contact contact = contactRepository.findById("contactId").orElseThrow();

        Address address = new Address();
        address.setId("addressId");
        address.setContact(contact);
        address.setStreet("jl.manju mundur");
        address.setCity("prembun");
        address.setProvince("jawa");
        address.setCountry("indonesia");
        address.setPostalCode("15155");
        addressRepository.save(address);

        mockMvc.perform(
                get("/api/contacts/contactId/address/" + address.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header("X-API-TOKEN", "contact")
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<AddressResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            Assertions.assertNull(response.getErrors());

            Assertions.assertEquals(address.getStreet(), response.getData().getStreet());
            Assertions.assertEquals(address.getCity(), response.getData().getCity());
            Assertions.assertEquals(address.getProvince(), response.getData().getProvince());
            Assertions.assertEquals(address.getCountry(), response.getData().getCountry());
            Assertions.assertEquals(address.getPostalCode(), response.getData().getPostalCode());

            Assertions.assertTrue(addressRepository.existsById(response.getData().getId()));

        });

        /**
         * Response Body = {"data":{"id":"addressId","street":"jl.manju mundur","city":"prembun","province":"jawa","country":"indonesia","postalCode":"15155"},"errors":null,"paging":null}
         */
    }

    @Test
    void testUpdateAddressNotBlank() throws Exception {

        // select id contact
        Contact contact = contactRepository.findById("contactId").orElseThrow();

        Address address = new Address();
        address.setId("addressId");
        address.setContact(contact);
        address.setStreet("jl.manju mundur");
        address.setCity("prembun");
        address.setProvince("jawa");
        address.setCountry("indonesia");
        address.setPostalCode("15155");
        addressRepository.save(address);

        UpdateAddressRequest request = new UpdateAddressRequest();
        request.setCountry(""); // not blank constraint validation


        mockMvc.perform(
                put("/api/contacts/contactId/address/" + address.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("X-API-TOKEN", "contact")
        ).andExpectAll(
                status().isBadRequest()
        ).andDo(result -> {
            WebResponse<AddressResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            Assertions.assertNotNull(response.getErrors());

        });

        /**
         * Response Body = {"data":null,"errors":"country: must not be blank","paging":null}
         */
    }

    @Test
    void testUpdateAddressUnauthorized() throws Exception {

        // select id contact
        Contact contact = contactRepository.findById("contactId").orElseThrow();

        Address address = new Address();
        address.setId("addressId");
        address.setContact(contact);
        address.setStreet("jl.manju mundur");
        address.setCity("prembun");
        address.setProvince("jawa");
        address.setCountry("indonesia");
        address.setPostalCode("15155");
        addressRepository.save(address);

        mockMvc.perform(
                put("/api/contacts/contactId/address/" + address.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
            WebResponse<AddressResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            Assertions.assertNotNull(response.getErrors());

        });

        /**
         * Response Body = {"data":null,"errors":"Unauthorized","paging":null}
         */
    }

    @Test
    void testUpdateAddressSuccess() throws Exception {

        // select id contact
        Contact contact = contactRepository.findById("contactId").orElseThrow();

        Address address = new Address();
        address.setId("addressId");
        address.setContact(contact);
        address.setStreet("jl.manju mundur");
        address.setCity("prembun");
        address.setProvince("jawa");
        address.setCountry("indonesia");
        address.setPostalCode("15155");
        addressRepository.save(address);

        UpdateAddressRequest request = new UpdateAddressRequest();
        request.setStreet("rubah jl.manju");
        request.setCity("rubah prembun");
        request.setProvince("rubah jawa");
        request.setCountry("rubah indonesia");
        request.setPostalCode("rbah 15155"); // max 10 char

        mockMvc.perform(
                put("/api/contacts/contactId/address/" + address.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("X-API-TOKEN", "contact")
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<AddressResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            Assertions.assertNull(response.getErrors());

            Assertions.assertEquals(request.getStreet(), response.getData().getStreet());
            Assertions.assertEquals(request.getCity(), response.getData().getCity());
            Assertions.assertEquals(request.getProvince(), response.getData().getProvince());
            Assertions.assertEquals(request.getCountry(), response.getData().getCountry());
            Assertions.assertEquals(request.getPostalCode(), response.getData().getPostalCode());

            Assertions.assertTrue(addressRepository.existsById(response.getData().getId()));

        });

        /**
         * Response Body = {"street":"rubah jl.manju","city":"rubah prembun","province":"rubah jawa","country":"rubah indonesia","postalCode":"rbah 15155"}
         */
    }

    @Test
    void testDeleteAddressUnauthorized() throws Exception {

        // select id contact
        Contact contact = contactRepository.findById("contactId").orElseThrow();

        Address address = new Address();
        address.setId("addressId");
        address.setContact(contact);
        address.setStreet("jl.manju mundur");
        address.setCity("prembun");
        address.setProvince("jawa");
        address.setCountry("indonesia");
        address.setPostalCode("15155");
        addressRepository.save(address);

        mockMvc.perform(
                delete("/api/contacts/contactId/address/" + address.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
            WebResponse<AddressResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            Assertions.assertNotNull(response.getErrors());

        });

        /**
         * Response Body = {"data":null,"errors":"Unauthorized","paging":null}
         */
    }

    @Test
    void testDeleteAddressIdNotIsFound() throws Exception {

        mockMvc.perform(
                delete("/api/contacts/contactId/address/IDasalasalan")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", "contact")
        ).andExpectAll(
                status().isNotFound()
        ).andDo(result -> {
            WebResponse<AddressResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            Assertions.assertNotNull(response.getErrors());

        });

        /**
         * // jika id contact salah akan response
         * Response Body = {"data":null,"errors":"Contact is not found","paging":null}
         * // jika id address salah akan response
         * Response Body = {"data":null,"errors":"Address is not found","paging":null}
         */
    }

    @Test
    void testDeleteAddressSuccess() throws Exception {

        // select id contact
        Contact contact = contactRepository.findById("contactId").orElseThrow();

        // alih alih kita save di awal
        Address address = new Address();
        address.setId("addressId");
        address.setContact(contact);
        address.setStreet("jl.manju mundur");
        address.setCity("prembun");
        address.setProvince("jawa");
        address.setCountry("indonesia");
        address.setPostalCode("15155");
        addressRepository.save(address);

        mockMvc.perform(
                delete("/api/contacts/contactId/address/" + address.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", "contact")
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            Assertions.assertNull(response.getErrors());
            Assertions.assertEquals("OK", response.getData());

            Assertions.assertFalse(addressRepository.existsById(address.getId()));

        });

        /**
         * Response Body = {"data":"OK","errors":null,"paging":null}
         */
    }


}