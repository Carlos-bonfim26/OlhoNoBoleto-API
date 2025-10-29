package com.example.OlhoNoBoleto.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.CascadeType;
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
@Table(name = "tb_boleto")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Boleto implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(nullable = false, unique = true)
    private String linhaDigitavel;
    @Column(nullable = false, unique = true)
    private String banco;
    @ManyToOne
    @JoinColumn(name = "beneficiarioID", nullable = false)
    private Beneficiario beneficiario;
    @Column(nullable = false)
    private Double valor;
    @Column(nullable = false)
    private LocalDateTime dataValidacao;
    @Column(nullable = false)
    private String statusValidacao;
    @OneToOne(mappedBy = "boleto", cascade = CascadeType.ALL)
    private Report report;

}