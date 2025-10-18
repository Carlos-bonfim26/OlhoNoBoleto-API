package com.example.OlhoNoBoleto.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import com.example.OlhoNoBoleto.model.Beneficiario;
import com.example.OlhoNoBoleto.repository.BeneficiarioRepository;

public class BeneficiarioService {
    @Autowired
    private BeneficiarioRepository beneficiarioRepository;

    public Optional<Beneficiario> buscarPorCnpjCpf(String cnpjCpf) {
        return Optional.empty();
    }

}