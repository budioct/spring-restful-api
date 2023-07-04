package com.tutorial.restful.api.service;

import com.tutorial.restful.api.dto.ContactResponse;
import com.tutorial.restful.api.dto.CreateContactRequest;
import com.tutorial.restful.api.entity.User;

public interface ContactService {

    ContactResponse create(User user, CreateContactRequest request);

}