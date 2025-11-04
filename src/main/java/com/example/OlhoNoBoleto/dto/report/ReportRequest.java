package com.example.OlhoNoBoleto.dto.report;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.validation.constraints.NotNull;
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
  @NotNull
  private UUID usuarioId;
  private String nomeUsuario;
  @NotNull
  private UUID boletoId;
  @NotNull
  private UUID beneficiarioId;
  @NotNull
  private String descricaoProblema;
  private LocalDateTime dataReport;
}
