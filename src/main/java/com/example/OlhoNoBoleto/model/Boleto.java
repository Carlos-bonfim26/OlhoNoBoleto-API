package com.example.OlhoNoBoleto.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
@Entity
@Table(name = "tb_boleto")
public class Boleto implements Serializable{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    @Column(nullable = false, unique = true)
    private String linhaDigitavel;
    @Column(nullable = false, unique = true)
    private String banco;
    @ManyToOne
    @JoinColumn(name = "beneficiarioID", nullable = false)
    private Beneficiario beneficiario;
    @Column(nullable = false)
    private Double valor;
    @Column(nullable = false)
    private LocalDateTime dataValidacao;
    @Column(nullable = false)
    private String statusValidacao;

    // Getters and Setters
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