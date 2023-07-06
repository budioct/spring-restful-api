package com.tutorial.restful.api.service;

import com.tutorial.restful.api.dto.AddressResponse;
import com.tutorial.restful.api.dto.CreateAddressRequest;
import com.tutorial.restful.api.dto.UpdateAddressRequest;
import com.tutorial.restful.api.entity.User;

public interface AddressService {

    AddressResponse create(User user, CreateAddressRequest request);

    AddressResponse get(User user, String contactId, String addressId);

    AddressResponse update(User user, UpdateAddressRequest request);

}
