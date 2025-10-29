package com.example.OlhoNoBoleto.dto.user;

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
    private String nome;
    private String email;
    private Role role;
}
