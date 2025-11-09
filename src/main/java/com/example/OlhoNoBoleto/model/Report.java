package com.example.OlhoNoBoleto.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

import com.example.OlhoNoBoleto.enums.ReportSeverity;
import com.example.OlhoNoBoleto.enums.ReportStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;

@Entity
@Table(name = "tb_report")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Report implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuarioID", nullable = false)
    private User usuario;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "boletoID", nullable = false)
    private Boleto boleto;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "beneficiarioID", nullable = false)
    private Beneficiario beneficiario;
    @Column(nullable = false)
    private String descricao;
    @Column(nullable = false)
    private LocalDateTime dataReport;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReportStatus status = ReportStatus.PENDENTE; // Campo adicionado com padrão PENDENTE
    @Column(nullable = false)
    private String titulo; // Resumo do problema
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReportSeverity severidade = ReportSeverity.MEDIA; // BAIXA, MEDIA, ALTA, CRITICA
    @Column
    private String categoria; // "Golpe", "Cobrança indevida", "Serviço não prestado", etc.
    @Column
    private LocalDateTime dataResolucao; // Quando o admin resolver
    @Column(length = 1000)
    private String observacaoAdmin; // Comentário do admin
}
