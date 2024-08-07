package com.tutorial.restful.api.service;

import com.tutorial.restful.api.dto.ContactResponse;
import com.tutorial.restful.api.dto.CreateContactRequest;
import com.tutorial.restful.api.dto.SearchContactRequest;
import com.tutorial.restful.api.dto.UpdateContactRequest;
import com.tutorial.restful.api.entity.User;
import org.springframework.data.domain.Page;

public interface ContactService {

    ContactResponse create(User user, CreateContactRequest request);

    ContactResponse get(User user, String id);

    ContactResponse update(User user, UpdateContactRequest request);

    void delete(User user, String contactId);

    Page<ContactResponse> search(User user, SearchContactRequest request);


}
