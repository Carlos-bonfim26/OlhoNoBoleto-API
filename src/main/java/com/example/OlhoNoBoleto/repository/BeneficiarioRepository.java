package com.example.OlhoNoBoleto.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.OlhoNoBoleto.model.Beneficiario;

import java.util.List;


public interface BeneficiarioRepository extends JpaRepository<Beneficiario, UUID> {
    List<Beneficiario> findByDocument(String document);
}
