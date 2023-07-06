package com.tutorial.restful.api.repository;

import com.tutorial.restful.api.entity.Address;
import com.tutorial.restful.api.entity.Contact;
import com.tutorial.restful.api.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AddressRepository extends JpaRepository<Address, String> {

    // method query
    Optional<Address> findFirstByContactAndId(Contact contact, String contactId);

}
