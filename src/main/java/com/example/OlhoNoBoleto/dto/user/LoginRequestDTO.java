package com.example.OlhoNoBoleto.dto.user;

public class LoginRequestDTO {
    private String email;
    private String senha;

    // Construtor padrão
    public LoginRequestDTO() {}

    // Construtor com argumentos (opcional)
    public LoginRequestDTO(String email, String senha) {
        this.email = email;
        this.senha = senha;
    }

    // Getters e Setters
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }
}
