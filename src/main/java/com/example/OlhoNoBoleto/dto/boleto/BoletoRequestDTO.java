package com.example.OlhoNoBoleto.dto.boleto;

public class BoletoRequestDTO {
    private String linhaDigitavel;
    private String banco;
    private Long beneficiarioId;
    private Double valor;

    public BoletoRequestDTO() {
    }

    public BoletoRequestDTO(String linhaDigitavel, String banco, Long beneficiarioId, Double valor) {
        this.linhaDigitavel = linhaDigitavel;
        this.banco = banco;
        this.beneficiarioId = beneficiarioId;
        this.valor = valor;
    }
}
