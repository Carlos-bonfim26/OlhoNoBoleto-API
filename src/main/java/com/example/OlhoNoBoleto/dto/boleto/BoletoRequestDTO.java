package com.example.OlhoNoBoleto.dto.boleto;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BoletoRequestDTO {
    private UUID id;
    @NotBlank(message = "Linha digitável é obrigatória")
    private String linhaDigitavel;
    @NotBlank(message = "Banco é obrigatório")
    private String banco;
    @NotBlank(message = "ID do beneficiário é obrigatório")
    private String beneficiarioId;
    @NotNull(message = "Valor é obrigatório")
    private Double valor;
}
