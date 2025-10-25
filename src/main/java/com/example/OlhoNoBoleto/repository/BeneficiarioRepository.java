package com.example.OlhoNoBoleto.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.OlhoNoBoleto.model.Beneficiario;

public interface BeneficiarioRepository extends JpaRepository<Beneficiario, UUID> {
    Optional<Beneficiario> findByCnpjCpf(String cnpjCpf);
}
