package com.example.OlhoNoBoleto.controller;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.OlhoNoBoleto.dto.report.ReportRequest;
import com.example.OlhoNoBoleto.dto.report.ReportResponseDTO;
import com.example.OlhoNoBoleto.repository.ReportRepository;
import com.example.OlhoNoBoleto.service.ReportService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/report")
@RequiredArgsConstructor
public class ReportController {

        private final ReportRepository reportRepository;
        private final ReportService reportService;

        @GetMapping("/reports")
        public ResponseEntity<?> mostrarReports() {
                var allReports = reportRepository.findAll();
                return ResponseEntity.ok(allReports);
        }

        @PostMapping("/criarReport")
        public ResponseEntity<ReportResponseDTO> criarReport(@RequestBody @Valid ReportRequest request) {
                ReportResponseDTO response = reportService.criarReport(request);
                return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }

        @PutMapping("/{id}")
        public ResponseEntity<ReportResponseDTO> atualizarDescricao(@PathVariable UUID id,
                        @RequestBody String novaDescricao) {
                ReportResponseDTO atualizado = reportService.atualizarDescricao(id, novaDescricao);
                return ResponseEntity.ok(atualizado);
        }

        
}