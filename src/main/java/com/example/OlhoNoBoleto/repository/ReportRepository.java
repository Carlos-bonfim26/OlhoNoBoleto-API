package com.example.OlhoNoBoleto.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.OlhoNoBoleto.model.Report;

public interface ReportRepository extends JpaRepository<Report, Long> {
    Optional<Report> findByDescricao(String descricao);
}
