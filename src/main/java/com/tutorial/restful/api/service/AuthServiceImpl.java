package com.tutorial.restful.api.service;

import com.tutorial.restful.api.dto.LoginUserRequest;
import com.tutorial.restful.api.dto.TokenResponse;
import com.tutorial.restful.api.entity.User;
import com.tutorial.restful.api.repository.UserRepository;
import com.tutorial.restful.api.security.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserRepository userRepository; // service entity dan spring data jpa

    @Autowired
    private ValidationService validationService; // service constraint validation

    @Transactional // kita buat behavior menjadi deklaratif
    public TokenResponse login(LoginUserRequest request) {

        validationService.validate(request); // cek jika ada field yg null // akan menangkap constraint validation

        // cek apakah user ada dengan username di DB
        // jika tidak ada. tingal return Exception username tidak ada. dan beri pesan kesalahan
        User user = userRepository.findById(request.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Username or password wrong"));

        // cek password apakah di encript jika iya
        // beri token dengan UUID dan waktu token Expired
        if (BCrypt.checkpw(request.getPassword(), user.getPassword())) {
            user.setToken(UUID.randomUUID().toString());
            user.setTokenExpiredAt(next2Days());
            userRepository.save(user);

            return TokenResponse
                    .builder()
                    .token(user.getToken())
                    .expiredAt(user.getTokenExpiredAt())
                    .build();
        } else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Username or password wrong");
        }

    }

    private long next2Days() {
        // waktu 2 hari dalam milisecond
        return System.currentTimeMillis() + (1000 * 16 * 24 * 2);
    }

    @Transactional
    public void logout(User user) {
        // karena user yang login punya token dan tokenExpireAt kita tinggal replace saja
        user.setToken(null);
        user.setTokenExpiredAt(null);

        userRepository.save(user);
    }



}
