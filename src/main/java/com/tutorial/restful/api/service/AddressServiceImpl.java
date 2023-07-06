package com.tutorial.restful.api.service;

import com.tutorial.restful.api.dto.AddressResponse;
import com.tutorial.restful.api.dto.CreateAddressRequest;
import com.tutorial.restful.api.dto.UpdateAddressRequest;
import com.tutorial.restful.api.entity.Address;
import com.tutorial.restful.api.entity.Contact;
import com.tutorial.restful.api.entity.User;
import com.tutorial.restful.api.repository.AddressRepository;
import com.tutorial.restful.api.repository.ContactRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AddressServiceImpl implements AddressService {

    @Autowired
    private ContactRepository contactRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private ValidationService validationService;

    @Transactional
    public AddressResponse create(User user, CreateAddressRequest request) {

        validationService.validate(request); // menangkap constraint validasi

        Contact contact = contactRepository.findFirstByUserAndId(user, request.getContactId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Contact is not found"));

        Address address = new Address();
        address.setId(UUID.randomUUID().toString());
        address.setContact(contact);
        address.setStreet(request.getStreet());
        address.setCity(request.getCity());
        address.setProvince(request.getProvince());
        address.setCountry(request.getCountry());
        address.setPostalCode(request.getPostalCode());
        addressRepository.save(address); // proses DB

        return toAddressResponse(address);
    }

    @Transactional(readOnly = true)
    public AddressResponse get(User user, String contactId, String addressId) {

        // user many contact
        Contact contact = contactRepository.findFirstByUserAndId(user, contactId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Contact is not found"));

        // contact many address
        Address address = addressRepository.findFirstByContactAndId(contact, addressId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Address is not found"));

        return toAddressResponse(address);
    }

    @Transactional
    public AddressResponse update(User user, UpdateAddressRequest request) {

        validationService.validate(request);

        // user many contact
        Contact contact = contactRepository.findFirstByUserAndId(user, request.getContactId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Contact is not found"));

        // contact many address
        Address address = addressRepository.findFirstByContactAndId(contact, request.getAddressId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Address is not found"));

        log.info("contact id= {}", contact.getId());
        log.info("address id= {}", address.getId());
        log.info("address object= {}", address);

        address.setStreet(request.getStreet());
        address.setCity(request.getCity());
        address.setProvince(request.getProvince());
        address.setCountry(request.getCountry());
        address.setPostalCode(request.getPostalCode());
        addressRepository.save(address); // proses DB

        return toAddressResponse(address);
    }

    @Transactional
    public void delete(User user, String contactId, String addressId) {

        // user many contact
        Contact contact = contactRepository.findFirstByUserAndId(user, contactId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Contact is not found"));

        // contact many address
        Address address = addressRepository.findFirstByContactAndId(contact, addressId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Address is not found"));

        log.info("contact id= {}", contact.getId());
        log.info("address id= {}", address.getId());

        addressRepository.delete(address); // proses DB

    }

    @Transactional(readOnly = true)
    public List<AddressResponse> listAddress(User user, String contactId) {

        Contact contact = contactRepository.findFirstByUserAndId(user, contactId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Contact is not found"));

        List<Address> list = addressRepository.findAllByContact(contact); // karena return value List<T>, kita perlu return entity Address ke AddressResponse. kita perlu menggunakan operasi Function<R,T> dari lambda

        // anonymouse method.
        // Function<T, R>
        // R apply(T t);
        return list.stream().map(new Function<Address, AddressResponse>() {
            @Override
            public AddressResponse apply(Address address) {
                return toAddressResponse(address); // konversi return dari List<Address> ke List<AddressResponse>
            }
        }).collect(Collectors.toList());

        // lambda
        // return list.stream().map(address -> {
        //    return toAddressResponse(address); // konversi return dari List<Address> ke List<AddressResponse>
        // }).collect(Collectors.toList());

        // method reference
        // konversi return dari List<Address> ke List<AddressResponse>
        //return list.stream().map(this::toAddressResponse).collect(Collectors.toList());
    }

    private AddressResponse toAddressResponse(Address address) {
        return AddressResponse.builder()
                .id(address.getId())
                .street(address.getStreet())
                .city(address.getCity())
                .province(address.getProvince())
                .country(address.getCountry())
                .postalCode(address.getPostalCode())
                .build();
    }


}
