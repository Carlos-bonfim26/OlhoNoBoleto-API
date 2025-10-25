package com.example.OlhoNoBoleto.dto.report;

import java.time.LocalDateTime;
import java.util.UUID;

public class ReportResponseDTO {
    private UUID id;
    private String nomeUsuario;
    private UUID boletoId;
    private String descricaoProblema;
    private LocalDateTime dataReport;

    public ReportResponseDTO(UUID id, String nomeUsuario, UUID boletoId, String descricaoProblema, LocalDateTime dataReport) {
        this.id = id;
        this.nomeUsuario = nomeUsuario;
        this.boletoId = boletoId;
        this.descricaoProblema = descricaoProblema;
        this.dataReport = dataReport;
    }

    // getters e setters
    public UUID getId() {
        return id;
    }
    public String getNomeUsuario() {
        return nomeUsuario;
    }
    public UUID getBoletoId() {
        return boletoId;
    }
    public String getDescricaoProblema() {
        return descricaoProblema;
    }
    public LocalDateTime getDataReport() {
        return dataReport;
    }
    public void setNomeUsuario(String nomeUsuario) {
        this.nomeUsuario = nomeUsuario;
    }
    public void setDescricaoProblema(String descricaoProblema) {
        this.descricaoProblema = descricaoProblema;
    }
    public void setDataReport(LocalDateTime dataReport) {
        this.dataReport = dataReport;
    }

}


