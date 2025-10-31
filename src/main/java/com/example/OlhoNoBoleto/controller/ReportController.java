package com.example.OlhoNoBoleto.controller;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.OlhoNoBoleto.dto.report.ReportRequest;
import com.example.OlhoNoBoleto.model.Boleto;
import com.example.OlhoNoBoleto.model.Report;
import com.example.OlhoNoBoleto.model.User;
import com.example.OlhoNoBoleto.repository.BoletoRepository;
import com.example.OlhoNoBoleto.repository.ReportRepository;
import com.example.OlhoNoBoleto.repository.UsuarioRepository;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/report")
public class ReportController {

    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private BoletoRepository boletoRepository;

    @GetMapping("/reports")
    public ResponseEntity<?> mostrarReports() {
        var allReports = reportRepository.findAll();
        return ResponseEntity.ok(allReports);
    }

    @PostMapping("/cadastroReport")
    public ResponseEntity<?> cadastro(@RequestBody @Valid ReportRequest reportRequest) {
        User usuario = usuarioRepository.findByNome(reportRequest.getNomeUsuario())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado com o nome: " + reportRequest.getNomeUsuario()));

        Boleto boleto = boletoRepository.findById(reportRequest.getBoletoId())
                .orElseThrow(() -> new RuntimeException("Boleto não encontrado com o ID: " + reportRequest.getBoletoId()));

        Report newReport = new Report();
        newReport.setUsuario(usuario);
        newReport.setBoleto(boleto);
        newReport.setDescricao(reportRequest.getDescricaoProblema());
        newReport.setDataReport(LocalDateTime.now());
        reportRepository.save(newReport);
        return ResponseEntity.ok(newReport);
    }

    @PutMapping("atualizar/{id}")
    public ResponseEntity<?> atualizarReport(@RequestBody @Valid ReportRequest reportRequest,
            @PathVariable UUID id) {
        Report updateReport = reportRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Report não encontrado com o ID: " + id));

        User usuario = usuarioRepository.findByNome(reportRequest.getNomeUsuario())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado com o nome: " + reportRequest.getNomeUsuario()));

        Boleto boleto = boletoRepository.findById(reportRequest.getBoletoId())
                .orElseThrow(() -> new RuntimeException("Boleto não encontrado com o ID: " + reportRequest.getBoletoId()));

        updateReport.setUsuario(usuario);
        updateReport.setBoleto(boleto);
        updateReport.setDescricao(reportRequest.getDescricaoProblema());
        updateReport.setDataReport(reportRequest.getDataReport());
        reportRepository.save(updateReport);
        return ResponseEntity.ok(updateReport);
    }

}