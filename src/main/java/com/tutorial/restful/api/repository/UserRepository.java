package com.tutorial.restful.api.repository;

import com.tutorial.restful.api.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

    // query method
    // query: select u.* form user u where u.token = ? limit?
    Optional<User> findFirstByToken(String token);

}
