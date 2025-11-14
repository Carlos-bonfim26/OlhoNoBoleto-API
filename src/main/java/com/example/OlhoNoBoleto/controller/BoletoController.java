package com.example.OlhoNoBoleto.controller;

import java.time.LocalDateTime;
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

import com.example.OlhoNoBoleto.dto.boleto.BoletoRequestDTO;
import com.example.OlhoNoBoleto.dto.boleto.BoletoResponseDTO;
import com.example.OlhoNoBoleto.dto.boleto.BoletoValidateRequestDTO;
import com.example.OlhoNoBoleto.model.Beneficiario;
import com.example.OlhoNoBoleto.model.Boleto;
import com.example.OlhoNoBoleto.repository.BeneficiarioRepository;
import com.example.OlhoNoBoleto.repository.BoletoRepository;
import com.example.OlhoNoBoleto.service.BoletoService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/boleto")
public class BoletoController {

    @Autowired
    private BoletoRepository boletoRepository;

    @Autowired
    private BeneficiarioRepository beneficiarioRepository;
    @Autowired
    private BoletoService boletoService;

    @PostMapping("/validate")
    public ResponseEntity<BoletoResponseDTO> validar(@RequestBody @Valid BoletoValidateRequestDTO request) {
        BoletoResponseDTO response = boletoService.validarBoleto(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/boletos")
    public ResponseEntity<?> mostrarBoletos() {
        var allBoletos = boletoRepository.findAll();
        return ResponseEntity.ok(allBoletos);
    }

    @PostMapping("/cadastroBoleto")
    public ResponseEntity<?> cadastro(@RequestBody @Valid BoletoRequestDTO boletoRequest) {
        if (boletoRepository.findByLinhaDigitavel(boletoRequest.getLinhaDigitavel()).isEmpty() == false) {
            return ResponseEntity.badRequest().body("Linha digitável já cadastrada");
        }

        Beneficiario beneficiario = beneficiarioRepository
                .findById(UUID.fromString(boletoRequest.getBeneficiarioId().toString()))
                .orElseThrow(() -> new RuntimeException(
                        "Beneficiário não encontrado com o ID: " + boletoRequest.getBeneficiarioId()));
        Boleto newBoleto = new Boleto();
        newBoleto.setLinhaDigitavel(boletoRequest.getLinhaDigitavel());
        newBoleto.setBanco(boletoRequest.getBanco());
        newBoleto.setBeneficiario(beneficiario);
        newBoleto.setValor(boletoRequest.getValor());
        newBoleto.setDataValidacao(LocalDateTime.now());
        newBoleto.setStatusValidacao("Pendente");
        boletoRepository.save(newBoleto);
        return ResponseEntity.ok(newBoleto);
    }

    @PutMapping("atualizar/{id}")
    public ResponseEntity<?> atualizarBoleto(@RequestBody @Valid BoletoRequestDTO boletoRequest,
            @PathVariable UUID id) {
        Boleto updateBoleto = boletoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Boleto não encontrado com o ID: " + id));
        Beneficiario beneficiario = beneficiarioRepository
                .findById(UUID.fromString(boletoRequest.getBeneficiarioId().toString()))
                .orElseThrow(() -> new RuntimeException(
                        "Beneficiário não encontrado com o ID: " + boletoRequest.getBeneficiarioId()));
        updateBoleto.setLinhaDigitavel(boletoRequest.getLinhaDigitavel());
        updateBoleto.setBanco(boletoRequest.getBanco());
        updateBoleto.setBeneficiario(beneficiario);
        updateBoleto.setValor(boletoRequest.getValor());
        boletoRepository.save(updateBoleto);
        return ResponseEntity.ok(updateBoleto);
    }

}
