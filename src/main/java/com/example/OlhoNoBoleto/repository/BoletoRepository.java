package com.example.OlhoNoBoleto.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.OlhoNoBoleto.model.Boleto;

public interface BoletoRepository extends JpaRepository<Boleto, Long> {
    Optional<Boleto> findByLinhaDigitavel(String linhaDigitavel);
}
