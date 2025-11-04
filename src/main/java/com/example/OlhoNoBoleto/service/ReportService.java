package com.example.OlhoNoBoleto.service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.OlhoNoBoleto.dto.report.ReportRequest;
import com.example.OlhoNoBoleto.dto.report.ReportResponseDTO;
import com.example.OlhoNoBoleto.model.Beneficiario;
import com.example.OlhoNoBoleto.model.Boleto;
import com.example.OlhoNoBoleto.model.Report;
import com.example.OlhoNoBoleto.model.User;
import com.example.OlhoNoBoleto.repository.ReportRepository;
import com.example.OlhoNoBoleto.repository.UsuarioRepository;

import jakarta.persistence.EntityNotFoundException;

import com.example.OlhoNoBoleto.repository.BoletoRepository;
import com.example.OlhoNoBoleto.repository.BeneficiarioRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final ReportRepository reportRepository;
    private final UsuarioRepository usuarioRepository;
    private final BoletoRepository boletoRepository;
    private final BeneficiarioRepository beneficiarioRepository;

    public ReportResponseDTO criarReport(ReportRequest request) {
        // Buscar as entidades associadas
        User usuario = usuarioRepository.findById(request.getUsuarioId())
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));

        Boleto boleto = boletoRepository.findById(request.getBoletoId())
                .orElseThrow(() -> new EntityNotFoundException("Boleto não encontrado"));

        Beneficiario beneficiario = beneficiarioRepository.findById(request.getBeneficiarioId())
                .orElseThrow(() -> new EntityNotFoundException("Beneficiário não encontrado"));

        // Criar o report
        Report report = new Report();
        report.setUsuario(usuario);
        report.setBoleto(boleto);
        report.setBeneficiario(beneficiario);
        report.setDescricao(request.getDescricaoProblema());
        report.setDataReport(LocalDateTime.now());

        reportRepository.save(report);

        // Retornar DTO de resposta
        return new ReportResponseDTO(
                report.getId(),
                report.getUsuario().getNome(),
                report.getBoleto().getId(),
                report.getBeneficiario().getId(),
                report.getDescricao(),
                report.getDataReport());

    }
}