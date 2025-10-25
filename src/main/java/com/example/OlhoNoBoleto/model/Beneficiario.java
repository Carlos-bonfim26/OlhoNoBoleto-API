package com.example.OlhoNoBoleto.model;

import java.io.Serializable;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "tb_beneficiario")
public class Beneficiario implements Serializable{
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    @Column(nullable = false)
    private String nome;
    @Column(nullable = false, unique = true)
    private String cnpjCpf;
    @Column(nullable = false, unique = true)
    private String banco;
    @Column(nullable = false)
    private String agencia;
    @Column
    private Integer totalQueixas;

    // Getters and Setters
    

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