package com.tutorial.restful.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserResponse {

    // class ini dibuat untuk response body, Success berhasil get data dengan menyertakan Header sebagai syarat
    // akan mendapat response token dan tokenExpiredAt
    // ketika client consume/hit endpoint akan di beri response seperti format di bawah

    private String username;

    private String name;

}
