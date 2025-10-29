package com.example.OlhoNoBoleto.dto.beneficiario;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BeneficiarioRequestDTO {
   private UUID id;
    private String nome;
    private String cnpjCpf;
    private String banco;
    private String agencia;
    private Integer totalQueixas;
}
