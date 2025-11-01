package com.example.OlhoNoBoleto.dto.user;

import java.util.UUID;

import com.example.OlhoNoBoleto.enums.Role;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserResponseDTO {
    private UUID id;
    private String nome;
    private String email;
    private Role role;
}
