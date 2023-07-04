package com.tutorial.restful.api.controller;

import com.tutorial.restful.api.dto.LoginUserRequest;
import com.tutorial.restful.api.dto.TokenResponse;
import com.tutorial.restful.api.dto.WebResponse;
import com.tutorial.restful.api.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {

    @Autowired
    AuthService authService;

    @PostMapping(
            path = "/api/auth/login",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<TokenResponse> login(@RequestBody LoginUserRequest request) {

        TokenResponse tokenResponse = authService.login(request); // TokenResponse login(LoginUserRequest request)

        return WebResponse.<TokenResponse>builder().data(tokenResponse).build(); // return {"data":{"token":"2dd32768-9bd9-45ed-a60e-c5eb0e6521bd","expiredAt":1688437213465}}
    }


}
