package com.example.OlhoNoBoleto.dto.beneficiario;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class BeneficiarioResponseDTO {
    private UUID id;
    private String nome;
    private String cnpjCpf;
    private String banco;
    private String agencia;
    private Integer totalQueixas;
}

