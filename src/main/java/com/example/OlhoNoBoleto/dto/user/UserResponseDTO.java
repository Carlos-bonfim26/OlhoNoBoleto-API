package com.example.OlhoNoBoleto.dto.user;

import com.example.OlhoNoBoleto.model.User;

public class UserResponseDTO {
    private String nome;
    private String email;

    public UserResponseDTO(User usuario) {
        this.nome = usuario.getNome();
        this.email = usuario.getEmail();
    }
}
