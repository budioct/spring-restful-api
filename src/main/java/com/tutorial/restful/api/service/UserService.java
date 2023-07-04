package com.tutorial.restful.api.service;

import com.tutorial.restful.api.dto.RegisterUserRequest;
import com.tutorial.restful.api.dto.UserResponse;
import com.tutorial.restful.api.entity.User;

public interface UserService {

    void register(RegisterUserRequest request); // karena register return nya hanya OK kita buat return void saja

    UserResponse get(User user); // karena get user return data username dan name kita buat return UserResponse

}
