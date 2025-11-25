package com.example.OlhoNoBoleto.dto.user;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class LoginResponseDTO {
    private String message;
    private String token; // 🔥 Adicionar token se usar JWT
    private String email;
    private String role;
    private String nome;  // 🔥 Adicionar nome
    private UUID id;      // 🔥 Adicionar ID
}