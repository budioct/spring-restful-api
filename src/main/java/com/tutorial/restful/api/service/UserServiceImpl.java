package com.tutorial.restful.api.service;

import com.tutorial.restful.api.Exception.ApiException;
import com.tutorial.restful.api.dto.RegisterUserRequest;
import com.tutorial.restful.api.dto.UserResponse;
import com.tutorial.restful.api.entity.User;
import com.tutorial.restful.api.repository.UserRepository;
import com.tutorial.restful.api.security.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class UserServiceImpl implements UserService{

    @Autowired
    private UserRepository userRepository; // service entity dan spring data jpa

    @Autowired
    private ValidationService validationService; // service constraint validation

    @Transactional // kita buat behavior menjadi deklaratif
    public void register(RegisterUserRequest request){

        validationService.validate(request); // cek jika ada field yg null // akan menangkap constraint validation

        // cek apakah user dengan nama username sudah ada
        // jika ada. tingal hasil Exception username sudah ada
        if (userRepository.existsById(request.getUsername())){
            // error
            // throw new ApiException("Username already registered");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username already registered"); // ResponseStatusException(HttpStatusCode status, @Nullable String reason) // Konstruktor dengan status respons dan alasan untuk menambahkan pesan pengecualian sebagai penjelasan.
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(BCrypt.hashpw(request.getPassword(), BCrypt.gensalt())); // password di enkrip. jagan simpan dalam bentuk plain text
        user.setName(request.getName());
        userRepository.save(user); // save db

    }

    @Override
    public UserResponse get(User user) {

        // karena hanya get saha kita tidak perlu merubah behaviornya
        // argument sudah di resolver jadi tidak perlu @Request header, @RequestParam, dll

        return UserResponse.builder()
                .username(user.getUsername())
                .name(user.getName())
                .build();

    }


}
