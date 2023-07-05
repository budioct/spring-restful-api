package com.tutorial.restful.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SearchContactRequest {

    // DTO untuk pencarian contact. berdasarkan field yang ada di bawah

    private String name;

    private String email;

    private String phone;

    private Integer page;

    private Integer size;

}
