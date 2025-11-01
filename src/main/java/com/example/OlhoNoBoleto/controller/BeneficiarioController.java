package com.example.OlhoNoBoleto.controller;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.OlhoNoBoleto.dto.beneficiario.BeneficiarioRequestDTO;
import com.example.OlhoNoBoleto.model.Beneficiario;
import com.example.OlhoNoBoleto.repository.BeneficiarioRepository;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/beneficiario")
public class BeneficiarioController {

    @Autowired
    private BeneficiarioRepository beneficiarioRepository;

    @GetMapping("/beneficiarios")
    public ResponseEntity<?> mostrarBeneficiarios() {
        var allBeneficiarios = beneficiarioRepository.findAll();
        return ResponseEntity.ok(allBeneficiarios);
    }

    @PostMapping("/cadastroBeneficiario")
    public ResponseEntity<?> cadastro(@RequestBody @Valid BeneficiarioRequestDTO beneficario) {
        if (beneficiarioRepository.findByCnpjCpf(beneficario.getCnpjCpf()).isEmpty() == false) {
            return ResponseEntity.badRequest().body("CNPJ/CPF já cadastrado");
        }
        Beneficiario newBeneficiario = new Beneficiario();
        newBeneficiario.setNome(beneficario.getNome());
        newBeneficiario.setCnpjCpf(beneficario.getCnpjCpf());
        newBeneficiario.setBanco(beneficario.getBanco());
        newBeneficiario.setAgencia(beneficario.getAgencia());
        newBeneficiario.setTotalQueixas(beneficario.getTotalQueixas());
        beneficiarioRepository.save(newBeneficiario);
        return ResponseEntity.ok(newBeneficiario);
    }

    @PutMapping("atualizar/{id}")
    public ResponseEntity<?> atualizarUsuario(@RequestBody @Valid BeneficiarioRequestDTO beneficario,
            @PathVariable UUID id) {
        Beneficiario updateBeneficiario = beneficiarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado com o ID: " + id));

        updateBeneficiario.setNome(beneficario.getNome());
        updateBeneficiario.setCnpjCpf(beneficario.getCnpjCpf());
        updateBeneficiario.setBanco(beneficario.getBanco());
        updateBeneficiario.setAgencia(beneficario.getAgencia());
        updateBeneficiario.setTotalQueixas(beneficario.getTotalQueixas());
        beneficiarioRepository.save(updateBeneficiario);
        return ResponseEntity.ok(updateBeneficiario);
    }

}
