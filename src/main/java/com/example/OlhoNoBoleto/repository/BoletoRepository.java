package com.example.OlhoNoBoleto.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.OlhoNoBoleto.model.Boleto;

public interface BoletoRepository extends JpaRepository<Boleto, UUID> {
    Optional<Boleto> findByLinhaDigitavel(String linhaDigitavel);
}
