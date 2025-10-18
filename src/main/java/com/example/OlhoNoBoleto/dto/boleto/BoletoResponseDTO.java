package com.example.OlhoNoBoleto.dto.boleto;

import java.time.LocalDateTime;

public class BoletoResponseDTO {
    private Long id;
    private String banco;
    private String beneficiarioNome;
    private Double valor;
    private LocalDateTime dataValidacao;
    private String statusValidacao;

    public BoletoResponseDTO(Long id, String banco, String beneficiarioNome, Double valor, LocalDateTime dataValidacao, String statusValidacao) {
        this.id = id;
        this.banco = banco;
        this.beneficiarioNome = beneficiarioNome;
        this.valor = valor;
        this.dataValidacao = dataValidacao;
        this.statusValidacao = statusValidacao;
    }
}
