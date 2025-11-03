package com.example.OlhoNoBoleto.dto.boleto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BoletoResponseDTO {
    private String linhaDigitavel;
    private String banco;
    private String beneficiarioNome;
    private Double valor;
    private LocalDateTime dataValidacao;
    private String statusValidacao;
    private String mensagem;

}
