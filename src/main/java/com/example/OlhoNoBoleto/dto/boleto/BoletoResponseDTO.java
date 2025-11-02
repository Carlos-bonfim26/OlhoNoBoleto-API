package com.example.OlhoNoBoleto.dto.boleto;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class BoletoResponseDTO {
    private UUID id;
    private String banco;
    private String beneficiarioNome;
    private Double valor;
    private LocalDateTime dataValidacao;
    private String statusValidacao;
}