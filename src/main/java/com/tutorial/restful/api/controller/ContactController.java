package com.tutorial.restful.api.controller;

import com.tutorial.restful.api.dto.ContactResponse;
import com.tutorial.restful.api.dto.CreateContactRequest;
import com.tutorial.restful.api.dto.UpdateContactRequest;
import com.tutorial.restful.api.dto.WebResponse;
import com.tutorial.restful.api.entity.User;
import com.tutorial.restful.api.service.ContactService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
public class ContactController {

    @Autowired
    private ContactService contactService;

    @PostMapping(
            path = "/api/contacts",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<ContactResponse> create(User user, @RequestBody CreateContactRequest request){

        ContactResponse contactResponse = contactService.create(user, request); // ContactResponse create(User user, CreateContactRequest request)

        return WebResponse.<ContactResponse>builder().data(contactResponse).build(); // return {"data":{"id":"91a6efb6-3c6f-4e4c-bafd-051b9410d1ce","firstName":"budhi oct","lastName":"octaviansyah","email":"budioct@contoh.com","phone":"08999912222"},"errors":null}

    }

    @GetMapping(
            path = "/api/contacts/{contactId}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<ContactResponse> get(User user, @PathVariable(name = "contactId") String id){

        ContactResponse contactResponse = contactService.get(user, id);

        return WebResponse.<ContactResponse>builder().data(contactResponse).build(); // return

    }

    @PutMapping(
            path = "/api/contacts/{contactId}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<ContactResponse> update(User user,
                                               @RequestBody UpdateContactRequest request,
                                               @PathVariable(name = "contactId") String id){

        request.setId(id); // binding dari request body
        ContactResponse contactResponse = contactService.update(user, request); // ContactResponse update(User user, UpdateContactRequest request)

        return WebResponse.<ContactResponse>builder().data(contactResponse).build(); // return
    }




}
