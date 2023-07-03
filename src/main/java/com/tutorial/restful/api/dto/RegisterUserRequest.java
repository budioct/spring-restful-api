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
public class RegisterUserRequest {

    // DTO (Data Transfer Object) dimana Request dan Response, akan mengakses class ini.
    // bukan class Entity karna kebutuhan apa yang mau di kasih apa yang tidak mau di kasih

    @NotBlank // set constraint // tidak boleh blank/kosong
    @Size(max = 100) // set ukuran maksimal 100 karakter
    private String username;

    @NotBlank
    @Size(max = 100)
    private String password;

    @NotBlank
    @Size(max = 100)
    private String name;

}
