package com.example.OlhoNoBoleto.model;

import java.time.LocalDateTime;

public class Boleto {
    private Long id;
    private String linhaDigitavel;
    private String banco;
    private Beneficiario beneficiario;
    private Double valor;
    private LocalDateTime dataValidacao;
    private String statusValidacao;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public String getLinhaDigitavel() {
        return linhaDigitavel;
    }

    public String getBanco() {
        return banco;
    }

    public Beneficiario getBeneficiario() {
        return beneficiario;
    }

    public Double getValor() {
        return valor;
    }

    public LocalDateTime getDataValidacao() {
        return dataValidacao;
    }

    public String getStatusValidacao() {
        return statusValidacao;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public void setLinhaDigitavel(String linhaDigitavel) {
        this.linhaDigitavel = linhaDigitavel;
    }

    public void setBanco(String banco) {
        this.banco = banco;
    }

    public void setBeneficiario(Beneficiario beneficiario) {
        this.beneficiario = beneficiario;
    }

    public void setValor(Double valor) {
        this.valor = valor;
    }

    public void setDataValidacao(LocalDateTime dataValidacao) {
        this.dataValidacao = dataValidacao;
    }

    public void setStatusValidacao(String statusValidacao) {
        this.statusValidacao = statusValidacao;
    }

}