package com.example.OlhoNoBoleto.controller;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.OlhoNoBoleto.enums.ReportStatus;

import com.example.OlhoNoBoleto.dto.report.ReportRequest;
import com.example.OlhoNoBoleto.dto.report.ReportResponseDTO;
import com.example.OlhoNoBoleto.repository.UsuarioRepository;
import com.example.OlhoNoBoleto.model.Report;
import com.example.OlhoNoBoleto.model.User;
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
        private final UsuarioRepository usuarioRepository;
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

        @GetMapping("/admin/reports")
        @PreAuthorize("hasRole('ADMIN')")
        public ResponseEntity<List<ReportResponseDTO>> listarTodosReports() {
                List<ReportResponseDTO> reports = reportService.listarTodosReports();
                return ResponseEntity.ok(reports);
        }

        @PutMapping("/admin/{id}/status")
        @PreAuthorize("hasRole('ADMIN')")
        public ResponseEntity<ReportResponseDTO> atualizarStatus(@PathVariable UUID id,
                        @RequestBody ReportStatus novoStatus) {
                ReportResponseDTO atualizado = reportService.atualizarStatus(id, novoStatus);
                return ResponseEntity.ok(atualizado);
        }

        @GetMapping("/usuario/meus-reports")
        public ResponseEntity<List<ReportResponseDTO>> meusReports(Authentication authentication) {
                User usuario = getUsuarioFromAuthentication(authentication);
                List<Report> reports = reportRepository.findByUsuario(usuario);
                return ResponseEntity.ok(reports.stream()
                                .map(ReportResponseDTO::fromEntity)
                                .collect(Collectors.toList()));
        }

        private User getUsuarioFromAuthentication(Authentication authentication) {
                String email = authentication.getName();
                return usuarioRepository.findByEmail(email)
                                .orElseThrow(() -> new RuntimeException("Usuário não encontrado: " + email));
        }

        @GetMapping("/admin/dashboard")
        @PreAuthorize("hasRole('ADMIN')")
        public ResponseEntity<Map<String, Object>> dashboardAdmin() {
                long totalReports = reportRepository.count();
                long reportsPendentes = reportRepository.countByStatus(ReportStatus.PENDENTE);
                long reportsValidados = reportRepository.countByStatus(ReportStatus.VALIDADO);
                long reportsRejeitados = reportRepository.countByStatus(ReportStatus.FALSO);

                Map<String, Object> dashboard = Map.of(
                                "totalReports", totalReports,
                                "reportsPendentes", reportsPendentes,
                                "reportsValidados", reportsValidados,
                                "reportsRejeitados", reportsRejeitados,
                                "topBeneficiarios", reportRepository.findTopBeneficiariosComMaisReports());

                return ResponseEntity.ok(dashboard);
        }
}