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
public class UpdateUserRequest {

    // DTO
    // karena username tidak boleh dirubah saat pertama kali register. jadi yang boleh di ubah name dan password
    // karena update hanya request username dan password dan opsional yang dirubah bisa salah satu saja atau dua duanya
    // response nya adalah mendapat username dan name setelah di update

    @NotBlank // set constraint // tidak boleh blank/kosong
    @Size(max = 100) // set ukuran maksimal 100 karakter
    private String name;

    @NotBlank
    @Size(max = 100)
    private String password;

}
