package com.tutorial.restful.api.service;

import com.tutorial.restful.api.dto.ContactResponse;
import com.tutorial.restful.api.dto.CreateContactRequest;
import com.tutorial.restful.api.dto.UpdateContactRequest;
import com.tutorial.restful.api.entity.Contact;
import com.tutorial.restful.api.entity.User;
import com.tutorial.restful.api.repository.ContactRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
public class ContactServiceImpl implements ContactService {

    @Autowired
    private ContactRepository contactRepository;

    @Autowired
    private ValidationService validationService;

    @Transactional
    public ContactResponse create(User user, CreateContactRequest request) {

        validationService.validate(request); // tangkap constraint validation

        Contact contact = new Contact();
        contact.setId(UUID.randomUUID().toString());
        contact.setFirstName(request.getFirstName());
        contact.setLastName(request.getLastName());
        contact.setEmail(request.getEmail());
        contact.setPhone(request.getPhone());
        contact.setUser(user);

        contactRepository.save(contact); // save DB

        return toContactResponse(contact);
    }

    // method response Contact Response
    private ContactResponse toContactResponse(Contact contact) {
        return ContactResponse.builder()
                .id(contact.getId())
                .firstName(contact.getFirstName())
                .lastName(contact.getLastName())
                .email(contact.getEmail())
                .phone(contact.getPhone())
                .build(); // response
    }

    @Transactional(readOnly = true) // readOnly() = true Bendera boolean yang dapat disetel ke true jika transaksi hanya dapat dibaca secara efektif, memungkinkan pengoptimalan yang sesuai saat runtime.
    public ContactResponse get(User user, String id) {

        Contact contact = contactRepository.findFirstByUserAndId(user, id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Contact not found"));

        return toContactResponse(contact);

    }

    @Transactional
    public ContactResponse update(User user, UpdateContactRequest request) {

        validationService.validate(request); // tangkap constraint validation

        // cek apakah ada username dan id, jika ada kasih. jika tidak ada Exception dengan status
        Contact contact = contactRepository.findFirstByUserAndId(user, request.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Contact not found"));

        contact.setFirstName(request.getFirstName());
        contact.setLastName(request.getLastName());
        contact.setEmail(request.getEmail());
        contact.setPhone(request.getPhone());
        contactRepository.save(contact); // save DB

        return toContactResponse(contact);
    }


}
