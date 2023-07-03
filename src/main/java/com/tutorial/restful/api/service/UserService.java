package com.tutorial.restful.api.service;

import com.tutorial.restful.api.dto.RegisterUserRequest;

public interface UserService {

    void register(RegisterUserRequest request); // karena register return nya hanya OK kita buat void saja

}
