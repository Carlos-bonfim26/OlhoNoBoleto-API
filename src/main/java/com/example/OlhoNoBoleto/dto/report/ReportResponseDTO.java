package com.example.OlhoNoBoleto.dto.report;

import java.time.LocalDateTime;

public class ReportResponseDTO {
    private Long id;
    private String nomeUsuario;
    private Long boletoId;
    private String descricaoProblema;
    private LocalDateTime dataReport;

    public ReportResponseDTO(Long id, String nomeUsuario, Long boletoId, String descricaoProblema, LocalDateTime dataReport) {
        this.id = id;
        this.nomeUsuario = nomeUsuario;
        this.boletoId = boletoId;
        this.descricaoProblema = descricaoProblema;
        this.dataReport = dataReport;
    }

    // getters e setters
    public Long getId() {
        return id;
    }
    public String getNomeUsuario() {
        return nomeUsuario;
    }
    public Long getBoletoId() {
        return boletoId;
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
    public void setNomeUsuario(String nomeUsuario) {
        this.nomeUsuario = nomeUsuario;
    }
    public void setBoletoId(Long boletoId) {
        this.boletoId = boletoId;
    }
    public void setDescricaoProblema(String descricaoProblema) {
        this.descricaoProblema = descricaoProblema;
    }
    public void setDataReport(LocalDateTime dataReport) {
        this.dataReport = dataReport;
    }

}


