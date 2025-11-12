package com.example.OlhoNoBoleto.dto.report;

import java.time.LocalDateTime;
import java.util.UUID;

import com.example.OlhoNoBoleto.model.Report;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ReportResponseDTO {
   private UUID id;
    private String usuarioNome;
    private UUID usuarioId;
    private UUID boletoId;
    private UUID beneficiarioId;
    private String beneficiarioNome;  // ✅ Adicionar
    private String titulo;
    private String descricao;
    private String categoria;
    private String severidade;
    private LocalDateTime dataReport;
    private String status;
    private String linhaDigitavel;  // ✅ Para facilitar
    
    public static ReportResponseDTO fromEntity(Report report) {
        return new ReportResponseDTO(
            report.getId(),
            report.getUsuario().getNome(),
            report.getUsuario().getId(),
            report.getBoleto().getId(),
            report.getBeneficiario().getId(),
            report.getBeneficiario().getNome(),
            report.getTitulo(),
            report.getDescricao(),
            report.getCategoria(),
            report.getSeveridade() != null ? report.getSeveridade().name() : "MEDIA",
            report.getDataReport(),
            report.getStatus().name(),
            report.getBoleto().getLinhaDigitavel()
        );
    }
}
