package com.tutorial.restful.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginUserRequest {

    // DTO
    // karena login hanya request username dan password
    // response nya adalah mendapat token dan tokenExpiredAt

    @NotBlank // set constraint // tidak boleh blank/kosong
    @Size(max = 100) // set ukuran maksimal 100 karakter
    private String username;

    @NotBlank
    @Size(max = 100)
    private String password;

}
