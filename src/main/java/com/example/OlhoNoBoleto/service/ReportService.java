package com.example.OlhoNoBoleto.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.OlhoNoBoleto.dto.report.ReportRequest;
import com.example.OlhoNoBoleto.dto.report.ReportResponseDTO;
import com.example.OlhoNoBoleto.enums.ReportSeverity;
import com.example.OlhoNoBoleto.enums.ReportStatus;
import com.example.OlhoNoBoleto.exceptions.BusinessException;
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
                if (reportRepository.existsByUsuarioIdAndBoletoId(
                                request.getUsuarioId(), request.getBoletoId())) {
                        throw new IllegalArgumentException("Você já reportou este boleto anteriormente.");
                }

                // VALIDAR limite de reports por usuário (ex: 5 reports pendentes)
                long reportsPendentes = reportRepository.countByUsuarioIdAndStatus(
                                request.getUsuarioId(), ReportStatus.PENDENTE);
                if (reportsPendentes >= 5) {
                        throw new IllegalArgumentException("Você atingiu o limite de reports pendentes.");
                }
                if (reportRepository.existsByUsuarioIdAndBoletoId(request.getUsuarioId(), request.getBoletoId())) {
                        throw new BusinessException("Você já reportou este boleto anteriormente", "DUPLICATE_REPORT");
                }
                // Buscar as entidades associadas
                User usuario = usuarioRepository.findById(request.getUsuarioId())
                                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));

                Boleto boleto = boletoRepository.findById(request.getBoletoId())
                                .orElseThrow(() -> new EntityNotFoundException("Boleto não encontrado"));

                Beneficiario beneficiario = beneficiarioRepository.findById(request.getBeneficiarioId())
                                .orElseThrow(() -> new EntityNotFoundException("Beneficiário não encontrado"));

                // Criar report
                Report report = new Report();
                report.setUsuario(usuario);
                report.setBoleto(boleto);
                report.setBeneficiario(beneficiario);
                report.setTitulo(request.getTitulo());
                report.setDescricao(request.getDescricao());
                report.setCategoria(request.getCategoria());
                report.setSeveridade(ReportSeverity.MEDIA); // Padrão
                report.setDataReport(LocalDateTime.now());
                report.setStatus(ReportStatus.PENDENTE);

                reportRepository.save(report);

                // ATUALIZAR CONTADOR DO BENEFICIÁRIO
                atualizarContadorDenuncias(beneficiario);

                return ReportResponseDTO.fromEntity(report);

        }

        private void atualizarContadorDenuncias(Beneficiario beneficiario) {
                int totalReports = reportRepository.countByBeneficiarioAndStatus(
                                beneficiario, ReportStatus.VALIDADO);
                beneficiario.setTotalQueixas(totalReports);
                beneficiarioRepository.save(beneficiario);
        }

        public List<ReportResponseDTO> listarTodosReports() {
                List<Report> reports = reportRepository.findAll();
                return reports.stream()
                                .map(ReportResponseDTO::fromEntity) // ✅ Mais limpo!
                                .collect(Collectors.toList());
        }

        public ReportResponseDTO atualizarStatus(UUID id, ReportStatus novoStatus) {
                Report report = reportRepository.findById(id)
                                .orElseThrow(() -> new EntityNotFoundException("Report não encontrado"));

                ReportStatus statusAnterior = report.getStatus();
                report.setStatus(novoStatus);

                // Lógica de atualização de contador
                if (novoStatus == ReportStatus.VALIDADO && statusAnterior != ReportStatus.VALIDADO) {
                        atualizarContadorDenuncias(report.getBeneficiario());
                }

                if (novoStatus == ReportStatus.VALIDADO || novoStatus == ReportStatus.FALSO) {
                        report.setDataResolucao(LocalDateTime.now());
                }

                reportRepository.save(report);

                return ReportResponseDTO.fromEntity(report); // ✅ Mais limpo!
        }

        public ReportResponseDTO atualizarDescricao(UUID id, String novaDescricao) {
                Report report = reportRepository.findById(id)
                                .orElseThrow(() -> new EntityNotFoundException("Report não encontrado"));

                report.setDescricao(novaDescricao);
                reportRepository.save(report);

                return ReportResponseDTO.fromEntity(report); // ✅ Usando fromEntity
        }
}
