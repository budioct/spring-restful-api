package com.tutorial.restful.api.controller;

import com.tutorial.restful.api.dto.AddressResponse;
import com.tutorial.restful.api.dto.CreateAddressRequest;
import com.tutorial.restful.api.dto.UpdateAddressRequest;
import com.tutorial.restful.api.dto.WebResponse;
import com.tutorial.restful.api.entity.User;
import com.tutorial.restful.api.service.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
public class AddressController {

    @Autowired
    private AddressService addressService;

    @PostMapping(
            path = "/api/contacts/{contactId}/addresses",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<AddressResponse> create(User user,
                                      @RequestBody CreateAddressRequest request,
                                      @PathVariable(name = "contactId") String contactId ){

        request.setContactId(contactId); // set id ke address
        AddressResponse addressResponse = addressService.create(user, request);

        return WebResponse.<AddressResponse>builder().data(addressResponse).build(); // return
    }

    @GetMapping(
            path = "/api/contacts/{contactId}/address/{addressId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<AddressResponse> get(User user,
                                            @PathVariable(name = "contactId") String contactId,
                                            @PathVariable(name = "addressId") String addressId
    ){

        AddressResponse addressResponse = addressService.get(user, contactId, addressId); // AddressResponse get(User user, String contactId, String addressId);

        return WebResponse.<AddressResponse>builder().data(addressResponse).build();

    }

    @PutMapping(
            path = "/api/contacts/{contactId}/address/{addressId}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<AddressResponse> update(User user,
                                               @RequestBody UpdateAddressRequest request,
                                               @PathVariable(name = "contactId") String contactId,
                                               @PathVariable(name = "addressId") String addressId) {
        request.setContactId(contactId); // binding id ke DTO request
        request.setAddressId(addressId);

        AddressResponse addressResponse = addressService.update(user, request); // AddressResponse update(User user, UpdateAddressRequest request);

        return WebResponse.<AddressResponse>builder().data(addressResponse).build();
    }

    @DeleteMapping(
            path = "/api/contacts/{contactId}/address/{addressId}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<String> delete(User user,
                                      @PathVariable(name = "contactId") String contactId,
                                      @PathVariable(name = "addressId") String addressId){

        addressService.delete(user, contactId, addressId); // void delete(User user, String contactId, String addressId)

        return WebResponse.<String>builder().data("OK").build();

    }




}
