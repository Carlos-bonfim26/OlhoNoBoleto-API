package com.example.OlhoNoBoleto.dto.boleto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BoletoRequestDTO {
    private String linhaDigitavel;
    private String banco;
    private Long beneficiarioId;
    private Double valor;
}
