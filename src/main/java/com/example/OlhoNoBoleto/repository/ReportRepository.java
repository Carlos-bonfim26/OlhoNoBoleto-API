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

    boolean existsByUsuarioIdAndBoletoId(UUID usuarioId, UUID boletoId);

    long countByUsuarioIdAndStatus(UUID usuarioId, ReportStatus status);

    List<Report> findByBeneficiarioAndStatus(Beneficiario beneficiario, ReportStatus status);

    List<Report> findByStatus(ReportStatus status);

    int countByBeneficiarioAndStatus(Beneficiario beneficiario, ReportStatus status);

    @Query("SELECT r.beneficiario, COUNT(r) FROM Report r WHERE r.status = 'VALIDADO' GROUP BY r.beneficiario ORDER BY COUNT(r) DESC")
    List<Object[]> findTopBeneficiariosComMaisReports();

    List<Report> findByUsuario(User usuario);

    List<Report> findByBeneficiario(Beneficiario beneficiario);

    long countByStatus(ReportStatus status);

    List<Report> findByUsuarioAndStatus(User usuario, ReportStatus status);

    List<Report> findByBoletoId(UUID boletoId);

    List<Report> findByStatusOrderByDataReportDesc(ReportStatus status);

    List<Report> findTop10ByOrderByDataReportDesc();
}
