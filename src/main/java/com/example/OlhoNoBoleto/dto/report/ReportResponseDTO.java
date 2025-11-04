package com.example.OlhoNoBoleto.dto.report;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ReportResponseDTO {
    private UUID id;
    private String nomeUsuario;
    private UUID boletoId;
    private UUID beneficiarioId;
    private String descricaoProblema;
    private LocalDateTime dataReport;

   
}
