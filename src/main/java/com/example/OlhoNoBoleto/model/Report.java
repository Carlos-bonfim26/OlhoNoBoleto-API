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
import jakarta.persistence.OneToOne;
@Entity
@Table(name = "tb_report")
public class Report implements Serializable{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    @ManyToOne
    @JoinColumn(name = "usuarioID", nullable = false)
    private User usuario;
    @OneToOne
    @JoinColumn(name = "boletoID", nullable = false)
    private Boleto boleto;
    @Column(nullable = false)
    private String descricaoProblema;
    @Column(nullable = false)
    private LocalDateTime dataReport;

    // Getters and Setters
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
