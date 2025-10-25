package com.example.OlhoNoBoleto.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.OlhoNoBoleto.model.Boleto;

public interface BoletoRepository extends JpaRepository<Boleto, UUID> {
    List<Boleto> findByLinhaDigitavel(String linhaDigitavel);
}
