package com.tutorial.restful.api.service;

import com.tutorial.restful.api.dto.LoginUserRequest;
import com.tutorial.restful.api.dto.TokenResponse;
import com.tutorial.restful.api.entity.User;

public interface AuthService {

    TokenResponse login(LoginUserRequest request); // karena login return nya hanya data token dan tokenExpiredAt kita buat TokenResponse

    void logout(User user);

}
