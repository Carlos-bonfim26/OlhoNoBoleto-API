package com.example.OlhoNoBoleto.controller;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.OlhoNoBoleto.dto.boleto.BoletoRequestDTO;
import com.example.OlhoNoBoleto.model.Boleto;
import com.example.OlhoNoBoleto.service.BoletoService;
@RestController
@RequestMapping("/boleto")
public class BoletoController {
    @Autowired
    private BoletoService boletoService;

    @PostMapping("/validate")
    public ResponseEntity<Boleto> validarBoleto(@RequestBody BoletoRequestDTO request) {
        return null;
    }
}
