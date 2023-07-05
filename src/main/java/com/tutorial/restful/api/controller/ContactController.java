package com.tutorial.restful.api.controller;

import com.tutorial.restful.api.dto.*;
import com.tutorial.restful.api.entity.User;
import com.tutorial.restful.api.service.ContactService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ContactController {

    @Autowired
    private ContactService contactService;

    @PostMapping(
            path = "/api/contacts",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<ContactResponse> create(User user, @RequestBody CreateContactRequest request) {

        ContactResponse contactResponse = contactService.create(user, request); // ContactResponse create(User user, CreateContactRequest request)

        return WebResponse.<ContactResponse>builder().data(contactResponse).build(); // return {"data":{"id":"91a6efb6-3c6f-4e4c-bafd-051b9410d1ce","firstName":"budhi oct","lastName":"octaviansyah","email":"budioct@contoh.com","phone":"08999912222"},"errors":null}

    }

    @GetMapping(
            path = "/api/contacts/{contactId}",
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<ContactResponse> get(User user, @PathVariable(name = "contactId") String id) {

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
                                               @PathVariable(name = "contactId") String id) {

        request.setId(id); // binding dari request body
        ContactResponse contactResponse = contactService.update(user, request); // ContactResponse update(User user, UpdateContactRequest request)

        return WebResponse.<ContactResponse>builder().data(contactResponse).build(); // return
    }

    @DeleteMapping(
            path = "/api/contacts/{contactId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<String> delete(User user, @PathVariable(name = "contactId") String contactId) {

        contactService.delete(user, contactId); // void delete(User user, String contactId)

        return WebResponse.<String>builder().data("OK").build(); // return
    }

    @GetMapping(
            path = "/api/contacts",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<List<ContactResponse>> search(User user,
                                                     @RequestParam(name = "name", required = false) String name,
                                                     @RequestParam(name = "email", required = false) String email,
                                                     @RequestParam(name = "phone", required = false) String phone,
                                                     @RequestParam(name = "page", required = false, defaultValue = "0") Integer page,
                                                     @RequestParam(name = "size", required = false, defaultValue = "10") Integer size
    ) {

        SearchContactRequest request = new SearchContactRequest();
        request.setName(name);
        request.setEmail(email);
        request.setPhone(phone);
        request.setPage(page);
        request.setSize(size);

        Page<ContactResponse> contactResponses = contactService.search(user, request);

        return WebResponse.<List<ContactResponse>>builder()
                .data(contactResponses.getContent())
                .paging(PagingResponse.builder()
                        .currentPage(contactResponses.getNumber())
                        .totalPage(contactResponses.getTotalPages())
                        .size(contactResponses.getSize())
                        .build())
                .build(); // return
    }

}
