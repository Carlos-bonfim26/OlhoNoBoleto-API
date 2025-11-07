package com.example.OlhoNoBoleto.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.OlhoNoBoleto.dto.report.ReportRequest;
import com.example.OlhoNoBoleto.dto.report.ReportResponseDTO;
import com.example.OlhoNoBoleto.enums.ReportStatus;
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
                report.setStatus(ReportStatus.PENDENTE);  // Definir status padrão

                reportRepository.save(report);

                // Retornar DTO de resposta
                return new ReportResponseDTO(
                                report.getId(),
                                report.getUsuario().getNome(),
                                report.getBoleto().getId(),
                                report.getBeneficiario().getId(),
                                report.getDescricao(),
                                report.getDataReport(),
                                report.getStatus());  // Incluir status na resposta

        }

        public ReportResponseDTO atualizarDescricao(UUID id, String novaDescricao) {
                Report report = reportRepository.findById(id)
                                .orElseThrow(() -> new EntityNotFoundException("Report não encontrado"));

                report.setDescricao(novaDescricao);
                reportRepository.save(report);

                return new ReportResponseDTO(
                                report.getId(),
                                report.getUsuario().getNome(),
                                report.getBoleto().getId(),
                                report.getBeneficiario().getId(),
                                report.getDescricao(),
                                report.getDataReport(),
                                report.getStatus());  // Incluir status na resposta
        }

        // Novo método para listar todos os reports (para admin)
        public List<ReportResponseDTO> listarTodosReports() {
                List<Report> reports = reportRepository.findAll();
                return reports.stream()
                        .map(report -> new ReportResponseDTO(
                                report.getId(),
                                report.getUsuario().getNome(),
                                report.getBoleto().getId(),
                                report.getBeneficiario().getId(),
                                report.getDescricao(),
                                report.getDataReport(),
                                report.getStatus()))
                        .collect(Collectors.toList());
        }

        // Novo método para atualizar status do report
        public ReportResponseDTO atualizarStatus(UUID id, ReportStatus novoStatus) {
                Report report = reportRepository.findById(id)
                                .orElseThrow(() -> new EntityNotFoundException("Report não encontrado"));

                report.setStatus(novoStatus);
                reportRepository.save(report);

                return new ReportResponseDTO(
                                report.getId(),
                                report.getUsuario().getNome(),
                                report.getBoleto().getId(),
                                report.getBeneficiario().getId(),
                                report.getDescricao(),
                                report.getDataReport(),
                                report.getStatus());
        }

}
