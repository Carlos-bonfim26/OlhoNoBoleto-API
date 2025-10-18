package com.example.OlhoNoBoleto.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class UserRequestDTO {
    @NotBlank
    private String nome;
    @Email
    private String email;
    @NotBlank
    private String senha;
}
