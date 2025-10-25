package com.example.OlhoNoBoleto.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.OlhoNoBoleto.model.Report;

public interface ReportRepository extends JpaRepository<Report, UUID> {
    Optional<Report> findByDescricao(String descricao);
}
