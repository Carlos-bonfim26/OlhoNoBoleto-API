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
  //Joaquim, bota os @NotNull onde for necessario
  private UUID id;
  private String usuarioNome;
  private UUID usuarioId;
  private UUID boletoId;
  private UUID beneficiarioId;
  private String beneficiarioNome; // ✅ Adicionar
  private String titulo;
  private String descricao;
  private String categoria;
  private String severidade;
  private LocalDateTime dataReport;
  private String status;
  private String linhaDigitavel; // ✅ Para facilitar

}
