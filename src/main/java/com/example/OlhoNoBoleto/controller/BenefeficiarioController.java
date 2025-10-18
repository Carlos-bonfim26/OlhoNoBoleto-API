package com.example.OlhoNoBoleto.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.example.OlhoNoBoleto.model.Beneficiario;
import com.example.OlhoNoBoleto.service.BeneficiarioService;

public class BenefeficiarioController {
    @Autowired
    private BeneficiarioService beneficiarioService;

    @GetMapping("/{cnpjCpf}")
    public ResponseEntity<Beneficiario> getBeneficiario(@PathVariable String cnpjCpf) {
        Optional<Beneficiario> beneficiario = beneficiarioService.buscarPorCnpjCpf(cnpjCpf);
        return beneficiario.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
