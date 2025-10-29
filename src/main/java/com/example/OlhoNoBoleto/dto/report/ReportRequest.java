package com.example.OlhoNoBoleto.dto.report;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReportRequest {
  private UUID id;
    private String nomeUsuario;
    private UUID boletoId;
    private String descricaoProblema;
    private LocalDateTime dataReport;
}
