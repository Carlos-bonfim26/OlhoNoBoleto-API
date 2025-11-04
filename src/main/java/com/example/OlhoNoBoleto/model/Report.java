package com.example.OlhoNoBoleto.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@AllArgsConstructor
@NoArgsConstructor
public class Report implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @ManyToOne
    @JoinColumn(name = "usuarioID", nullable = false)
    private User usuario;
    @OneToOne
    @JoinColumn(name = "boletoID", nullable = false)
    private Boleto boleto;
    @ManyToOne
    @JoinColumn(name = "beneficiarioID", nullable = false)
    private Beneficiario beneficiario;
    @Column(nullable = false)
    private String descricao;
    @Column(nullable = false)
    private LocalDateTime dataReport;

}
