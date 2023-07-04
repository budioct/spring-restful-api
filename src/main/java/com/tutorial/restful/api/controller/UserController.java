package com.tutorial.restful.api.controller;

import com.tutorial.restful.api.dto.RegisterUserRequest;
import com.tutorial.restful.api.dto.WebResponse;
import com.tutorial.restful.api.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping(
            path = "/api/users",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<String> userRegister(@RequestBody RegisterUserRequest request) {

        userService.register(request); // void register(RegisterUserRequest request)

        return WebResponse.<String>builder().data("OK").build(); // return {data : Oke}

    }

}
