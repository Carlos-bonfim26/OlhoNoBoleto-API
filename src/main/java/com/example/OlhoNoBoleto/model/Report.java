package com.example.OlhoNoBoleto.model;

import java.time.LocalDateTime;

public class Report {
    private Long id;
    private User usuario;
    private Boleto boleto;
    private String descricaoProblema;
    private LocalDateTime dataReport;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public User getUsuario() {
        return usuario;
    }

    public Boleto getBoleto() {
        return boleto;
    }

    public String getDescricaoProblema() {
        return descricaoProblema;
    }

    public LocalDateTime getDataReport() {
        return dataReport;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public void setUsuario(User usuario) {
        this.usuario = usuario;
    }

    public void setBoleto(Boleto boleto) {
        this.boleto = boleto;
    }

    public void setDescricaoProblema(String descricaoProblema) {
        this.descricaoProblema = descricaoProblema;
    }

    public void setDataReport(LocalDateTime dataReport) {
        this.dataReport = dataReport;
    }
}
