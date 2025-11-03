package com.example.OlhoNoBoleto.dto.boleto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BoletoValidateRequestDTO {
    @NotBlank(message = "Linha digitável é obrigatória")
    private String linhaDigitavel;

}
