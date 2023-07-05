package com.tutorial.restful.api.repository;

import com.tutorial.restful.api.entity.Contact;
import com.tutorial.restful.api.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ContactRepository extends JpaRepository<Contact, String>, JpaSpecificationExecutor<Contact> {

    /**
     * // JpaSpecificationExecutor jika ingin menggunakan fitur Specification perlu di Extends
     * Specification fitur Criteria untuk membuat Query secara dinamis di Spring Data JPA
     * ● Specification adalah lambda yang bisa kita buat dengan mengembalikan data JPA Predicate biasanya return value boolean
     * ● Kita bisa mendapatkan detail dari Root, CriteriaQuery dan CriteriaBuilder di method toPredicate() milik Specification
     */

    // query method
    Optional<Contact> findFirstByUserAndId(User user, String id);

}
