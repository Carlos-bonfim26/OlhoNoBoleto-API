package com.example.OlhoNoBoleto.dto.beneficiario;

import java.util.UUID;

public class BeneficiarioResponseDTO {
     private UUID id;
    private String nome;
    private String cnpjCpf;
    private String banco;
    private String agencia;
    private Integer totalQueixas;

    public BeneficiarioResponseDTO(UUID id, String nome, String cnpjCpf, String banco, String agencia, Integer totalQueixas) {
        this.id = id;
        this.nome = nome;
        this.banco = banco;
        this.agencia = agencia;
        this.totalQueixas = totalQueixas;
    }

   // getters and setters
    public UUID getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCnpjCpf() {
        return cnpjCpf;
    }

    public void setCnpjCpf(String cnpjCpf) {
        this.cnpjCpf = cnpjCpf;
    }

    public String getBanco() {
        return banco;
    }

    public void setBanco(String banco) {
        this.banco = banco;
    }

    public String getAgencia() {
        return agencia;
    }

    public void setAgencia(String agencia) {
        this.agencia = agencia;
    }

    public Integer getTotalQueixas() {
        return totalQueixas;
    }

    public void setTotalQueixas(Integer totalQueixas) {
        this.totalQueixas = totalQueixas;
    }
}
