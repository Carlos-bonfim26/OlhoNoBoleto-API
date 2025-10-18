package com.example.OlhoNoBoleto.model;

public class Beneficiario {
    private Long id;
    private String nome;
    private String cnpjCpf;
    private String banco;
    private String agencia;
    private Integer totalQueixas;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getCnpjCpf() {
        return cnpjCpf;
    }

    public String getBanco() {
        return banco;
    }

    public String getAgencia() {
        return agencia;
    }

    public Integer getTotalQueixas() {
        return totalQueixas;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setCnpjCpf(String cnpjCpf) {
        this.cnpjCpf = cnpjCpf;
    }

    public void setBanco(String banco) {
        this.banco = banco;
    }

    public void setAgencia(String agencia) {
        this.agencia = agencia;
    }

    public void setTotalQueixas(Integer totalQueixas) {
        this.totalQueixas = totalQueixas;
    }
}