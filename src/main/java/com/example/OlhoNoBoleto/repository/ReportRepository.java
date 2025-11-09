package com.example.OlhoNoBoleto.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.OlhoNoBoleto.enums.ReportStatus;
import com.example.OlhoNoBoleto.model.Beneficiario;
import com.example.OlhoNoBoleto.model.Report;
import com.example.OlhoNoBoleto.model.User;

public interface ReportRepository extends JpaRepository<Report, UUID> {
    Optional<Report> findByDescricao(String descricao);

    int countByBeneficiario(Beneficiario beneficiario);

    // NOVOS MÉTODOS PARA CONTROLE:
    boolean existsByUsuarioIdAndBoletoId(UUID usuarioId, UUID boletoId);

    long countByUsuarioIdAndStatus(UUID usuarioId, ReportStatus status);

    List<Report> findByBeneficiarioAndStatus(Beneficiario beneficiario, ReportStatus status);

    List<Report> findByStatus(ReportStatus status);

    int countByBeneficiarioAndStatus(Beneficiario beneficiario, ReportStatus status);

    // Para dashboard admin:
    @Query("SELECT r.beneficiario, COUNT(r) FROM Report r WHERE r.status = 'VALIDADO' GROUP BY r.beneficiario ORDER BY COUNT(r) DESC")
    List<Object[]> findTopBeneficiariosComMaisReports();
    // MÉTODOS QUE ESTAVAM FALTANDO:

    // ✅ Buscar reports por usuário
    List<Report> findByUsuario(User usuario);

    // ✅ Buscar reports por beneficiário
    List<Report> findByBeneficiario(Beneficiario beneficiario);

    // ✅ Contar reports por status
    long countByStatus(ReportStatus status);

    // ✅ Buscar reports por usuário e status
    List<Report> findByUsuarioAndStatus(User usuario, ReportStatus status);

    // ✅ Buscar reports por boleto
    List<Report> findByBoletoId(UUID boletoId);

    // ✅ Buscar reports pendentes
    List<Report> findByStatusOrderByDataReportDesc(ReportStatus status);

    // ✅ Buscar últimos reports (para dashboard)
    List<Report> findTop10ByOrderByDataReportDesc();
}
