package com.tutorial.restful.api.repository;

import com.tutorial.restful.api.entity.Contact;
import com.tutorial.restful.api.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ContactRepository extends JpaRepository<Contact, String> {

    // query method
    Optional<Contact> findFirstByUserAndId(User user, String id);

}
