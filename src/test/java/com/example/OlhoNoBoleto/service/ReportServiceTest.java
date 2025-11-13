package com.example.OlhoNoBoleto.service;

import com.example.OlhoNoBoleto.dto.report.ReportRequest;
import com.example.OlhoNoBoleto.dto.report.ReportResponseDTO;
import com.example.OlhoNoBoleto.enums.ReportSeverity;
import com.example.OlhoNoBoleto.enums.ReportStatus;
import com.example.OlhoNoBoleto.exceptions.BusinessException;
import com.example.OlhoNoBoleto.model.Beneficiario;
import com.example.OlhoNoBoleto.model.Boleto;
import com.example.OlhoNoBoleto.model.Report;
import com.example.OlhoNoBoleto.model.User;
import com.example.OlhoNoBoleto.repository.BeneficiarioRepository;
import com.example.OlhoNoBoleto.repository.BoletoRepository;
import com.example.OlhoNoBoleto.repository.ReportRepository;
import com.example.OlhoNoBoleto.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReportServiceTest {

    @Mock
    private ReportRepository reportRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private BoletoRepository boletoRepository;

    @Mock
    private BeneficiarioRepository beneficiarioRepository;

    @InjectMocks
    private ReportService reportService;

    private User criarUsuarioCompleto(UUID id) {
        User usuario = new User();
        usuario.setId(id);
        usuario.setNome("João Silva");
        usuario.setEmail("joao@email.com");
        return usuario;
    }

    private Boleto criarBoletoCompleto(UUID id) {
        Boleto boleto = new Boleto();
        boleto.setId(id);
        boleto.setLinhaDigitavel("00190000090144971819200300018284280000002000");
        boleto.setValor(1500.00);
        return boleto;
    }

    private Beneficiario criarBeneficiarioCompleto(UUID id) {
        Beneficiario beneficiario = new Beneficiario();
        beneficiario.setId(id);
        beneficiario.setNome("Banco do Brasil S.A.");
        beneficiario.setDocument("00000000000191");
        beneficiario.setTotalQueixas(0);
        return beneficiario;
    }

    private Report criarReportCompleto(UUID id, User usuario, Boleto boleto, Beneficiario beneficiario) {
        Report report = new Report();
        report.setId(id);
        report.setUsuario(usuario);
        report.setBoleto(boleto);
        report.setBeneficiario(beneficiario);
        report.setTitulo("Boleto Suspeito");
        report.setDescricao("Descrição do report");
        report.setCategoria("FRAUDE");
        report.setSeveridade(ReportSeverity.MEDIA);
        report.setStatus(ReportStatus.PENDENTE);
        report.setDataReport(LocalDateTime.now());
        return report;
    }

    // ✅ TESTE CORRIGIDO: Criar report com sucesso
    @Test
    void deveCriarReportComSucesso() {
        // Arrange
        UUID usuarioId = UUID.randomUUID();
        UUID boletoId = UUID.randomUUID();
        UUID beneficiarioId = UUID.randomUUID();

        ReportRequest request = new ReportRequest();
        request.setUsuarioId(usuarioId);
        request.setBoletoId(boletoId);
        request.setBeneficiarioId(beneficiarioId);
        request.setTitulo("Boleto Suspeito");
        request.setDescricao("Este boleto parece fraudulento");
        request.setCategoria("FRAUDE");

        User usuario = criarUsuarioCompleto(usuarioId);
        Boleto boleto = criarBoletoCompleto(boletoId);
        Beneficiario beneficiario = criarBeneficiarioCompleto(beneficiarioId);

        when(reportRepository.existsByUsuarioIdAndBoletoId(usuarioId, boletoId)).thenReturn(false);
        when(reportRepository.countByUsuarioIdAndStatus(usuarioId, ReportStatus.PENDENTE)).thenReturn(2L);
        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));
        when(boletoRepository.findById(boletoId)).thenReturn(Optional.of(boleto));
        when(beneficiarioRepository.findById(beneficiarioId)).thenReturn(Optional.of(beneficiario));
        when(reportRepository.save(any(Report.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(reportRepository.countByBeneficiarioAndStatus(any(Beneficiario.class), eq(ReportStatus.VALIDADO))).thenReturn(1);

        // Act
        ReportResponseDTO response = reportService.criarReport(request);

        // Assert - AGORA COMPARANDO STRINGS
        assertNotNull(response);
        assertEquals("Boleto Suspeito", response.getTitulo());
        assertEquals("Este boleto parece fraudulento", response.getDescricao());
        assertEquals("FRAUDE", response.getCategoria());
        assertEquals("PENDENTE", response.getStatus()); // ✅ AGORA É STRING
        assertEquals("MEDIA", response.getSeveridade()); // ✅ AGORA É STRING
        assertNotNull(response.getDataReport());

        verify(reportRepository, times(1)).save(any(Report.class));
        verify(beneficiarioRepository, times(1)).save(any(Beneficiario.class));
    }

    // ✅ TESTE CORRIGIDO: Não deve criar report quando usuário já reportou o boleto
    @Test
    void deveLancarExcecaoQuandoUsuarioJaReportouBoleto() {
        // Arrange
        UUID usuarioId = UUID.randomUUID();
        UUID boletoId = UUID.randomUUID();

        ReportRequest request = new ReportRequest();
        request.setUsuarioId(usuarioId);
        request.setBoletoId(boletoId);

        when(reportRepository.existsByUsuarioIdAndBoletoId(usuarioId, boletoId)).thenReturn(true);

        // Act & Assert - AGORA ESPERA IllegalArgumentException (não BusinessException)
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            reportService.criarReport(request);
        });

        assertEquals("Você já reportou este boleto anteriormente.", exception.getMessage());
        verify(reportRepository, never()).save(any(Report.class));
    }

    // ❌ REMOVIDO: deveLancarBusinessExceptionParaReportDuplicado
    // (Não é mais necessário pois o serviço lança IllegalArgumentException primeiro)

    // ✅ TESTE CORRIGIDO: Não deve criar report quando limite de reports pendentes é excedido
    @Test
    void deveLancarExcecaoQuandoLimiteReportsPendentesExcedido() {
        // Arrange
        UUID usuarioId = UUID.randomUUID();
        UUID boletoId = UUID.randomUUID();

        ReportRequest request = new ReportRequest();
        request.setUsuarioId(usuarioId);
        request.setBoletoId(boletoId);

        when(reportRepository.existsByUsuarioIdAndBoletoId(usuarioId, boletoId)).thenReturn(false);
        when(reportRepository.countByUsuarioIdAndStatus(usuarioId, ReportStatus.PENDENTE)).thenReturn(5L);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            reportService.criarReport(request);
        });

        assertEquals("Você atingiu o limite de reports pendentes.", exception.getMessage());
        verify(reportRepository, never()).save(any(Report.class));
    }

    // ✅ TESTE CORRIGIDO: Deve lançar exceção quando usuário não encontrado
    @Test
    void deveLancarExcecaoQuandoUsuarioNaoEncontrado() {
        // Arrange
        UUID usuarioId = UUID.randomUUID();
        UUID boletoId = UUID.randomUUID();

        ReportRequest request = new ReportRequest();
        request.setUsuarioId(usuarioId);
        request.setBoletoId(boletoId);

        when(reportRepository.existsByUsuarioIdAndBoletoId(usuarioId, boletoId)).thenReturn(false);
        when(reportRepository.countByUsuarioIdAndStatus(usuarioId, ReportStatus.PENDENTE)).thenReturn(0L);
        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.empty());

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            reportService.criarReport(request);
        });

        assertEquals("Usuário não encontrado", exception.getMessage());
    }

    // ✅ TESTE CORRIGIDO: Listar todos os reports
    @Test
    void deveListarTodosReports() {
        // Arrange
        User usuario = criarUsuarioCompleto(UUID.randomUUID());
        Boleto boleto = criarBoletoCompleto(UUID.randomUUID());
        Beneficiario beneficiario = criarBeneficiarioCompleto(UUID.randomUUID());

        Report report1 = criarReportCompleto(UUID.randomUUID(), usuario, boleto, beneficiario);
        Report report2 = criarReportCompleto(UUID.randomUUID(), usuario, boleto, beneficiario);

        when(reportRepository.findAll()).thenReturn(List.of(report1, report2));

        // Act
        List<ReportResponseDTO> response = reportService.listarTodosReports();

        // Assert
        assertNotNull(response);
        assertEquals(2, response.size());
        verify(reportRepository, times(1)).findAll();
    }

    // ✅ TESTE CORRIGIDO: Atualizar status com sucesso
    @Test
    void deveAtualizarStatusComSucesso() {
        // Arrange
        UUID reportId = UUID.randomUUID();
        User usuario = criarUsuarioCompleto(UUID.randomUUID());
        Boleto boleto = criarBoletoCompleto(UUID.randomUUID());
        Beneficiario beneficiario = criarBeneficiarioCompleto(UUID.randomUUID());
        
        Report report = criarReportCompleto(reportId, usuario, boleto, beneficiario);
        report.setStatus(ReportStatus.PENDENTE);

        when(reportRepository.findById(reportId)).thenReturn(Optional.of(report));
        when(reportRepository.save(any(Report.class))).thenReturn(report);
        when(reportRepository.countByBeneficiarioAndStatus(beneficiario, ReportStatus.VALIDADO)).thenReturn(3);

        // Act
        ReportResponseDTO response = reportService.atualizarStatus(reportId, ReportStatus.VALIDADO);

        // Assert - AGORA COMPARANDO STRINGS
        assertNotNull(response);
        assertEquals("VALIDADO", response.getStatus()); // ✅ AGORA É STRING
        assertNotNull(response.getDataReport());
        
        verify(reportRepository, times(1)).save(report);
        verify(beneficiarioRepository, times(1)).save(beneficiario);
    }

    // ✅ TESTE CORRIGIDO: Atualizar status para FALSO
    @Test
    void deveAtualizarStatusParaFalso() {
        // Arrange
        UUID reportId = UUID.randomUUID();
        User usuario = criarUsuarioCompleto(UUID.randomUUID());
        Boleto boleto = criarBoletoCompleto(UUID.randomUUID());
        Beneficiario beneficiario = criarBeneficiarioCompleto(UUID.randomUUID());
        
        Report report = criarReportCompleto(reportId, usuario, boleto, beneficiario);
        report.setStatus(ReportStatus.PENDENTE);

        when(reportRepository.findById(reportId)).thenReturn(Optional.of(report));
        when(reportRepository.save(any(Report.class))).thenReturn(report);

        // Act
        ReportResponseDTO response = reportService.atualizarStatus(reportId, ReportStatus.FALSO);

        // Assert - AGORA COMPARANDO STRINGS
        assertEquals("FALSO", response.getStatus()); // ✅ AGORA É STRING
        assertNotNull(response.getDataReport());
        verify(reportRepository, times(1)).save(report);
    }

    // ✅ TESTE CORRIGIDO: Deve lançar exceção quando report não encontrado ao atualizar status
    @Test
    void deveLancarExcecaoQuandoReportNaoEncontradoAoAtualizarStatus() {
        // Arrange
        UUID reportId = UUID.randomUUID();
        when(reportRepository.findById(reportId)).thenReturn(Optional.empty());

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            reportService.atualizarStatus(reportId, ReportStatus.VALIDADO);
        });

        assertEquals("Report não encontrado", exception.getMessage());
    }

    // ✅ TESTE CORRIGIDO: Atualizar descrição com sucesso
    @Test
    void deveAtualizarDescricaoComSucesso() {
        // Arrange
        UUID reportId = UUID.randomUUID();
        String novaDescricao = "Nova descrição detalhada";
        
        User usuario = criarUsuarioCompleto(UUID.randomUUID());
        Boleto boleto = criarBoletoCompleto(UUID.randomUUID());
        Beneficiario beneficiario = criarBeneficiarioCompleto(UUID.randomUUID());
        
        Report report = criarReportCompleto(reportId, usuario, boleto, beneficiario);
        report.setDescricao("Descrição antiga");

        when(reportRepository.findById(reportId)).thenReturn(Optional.of(report));
        when(reportRepository.save(any(Report.class))).thenReturn(report);

        // Act
        ReportResponseDTO response = reportService.atualizarDescricao(reportId, novaDescricao);

        // Assert
        assertNotNull(response);
        assertEquals(novaDescricao, response.getDescricao());
        verify(reportRepository, times(1)).save(report);
    }

    // ✅ TESTE CORRIGIDO: Deve lançar exceção quando report não encontrado ao atualizar descrição
    @Test
    void deveLancarExcecaoQuandoReportNaoEncontradoAoAtualizarDescricao() {
        // Arrange
        UUID reportId = UUID.randomUUID();
        when(reportRepository.findById(reportId)).thenReturn(Optional.empty());

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            reportService.atualizarDescricao(reportId, "Nova descrição");
        });

        assertEquals("Report não encontrado", exception.getMessage());
    }

    // ✅ TESTE CORRIGIDO: Verificar atualização do contador de denúncias
    @Test
    void deveAtualizarContadorDenunciasCorretamente() {
        // Arrange
        UUID usuarioId = UUID.randomUUID();
        UUID boletoId = UUID.randomUUID();
        UUID beneficiarioId = UUID.randomUUID();

        ReportRequest request = new ReportRequest();
        request.setUsuarioId(usuarioId);
        request.setBoletoId(boletoId);
        request.setBeneficiarioId(beneficiarioId);
        request.setTitulo("Teste");
        request.setDescricao("Teste");
        request.setCategoria("FRAUDE");

        User usuario = criarUsuarioCompleto(usuarioId);
        Boleto boleto = criarBoletoCompleto(boletoId);
        Beneficiario beneficiario = criarBeneficiarioCompleto(beneficiarioId);

        when(reportRepository.existsByUsuarioIdAndBoletoId(usuarioId, boletoId)).thenReturn(false);
        when(reportRepository.countByUsuarioIdAndStatus(usuarioId, ReportStatus.PENDENTE)).thenReturn(0L);
        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));
        when(boletoRepository.findById(boletoId)).thenReturn(Optional.of(boleto));
        when(beneficiarioRepository.findById(beneficiarioId)).thenReturn(Optional.of(beneficiario));
        when(reportRepository.save(any(Report.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(reportRepository.countByBeneficiarioAndStatus(beneficiario, ReportStatus.VALIDADO)).thenReturn(5);

        // Act
        reportService.criarReport(request);

        // Assert
        verify(beneficiarioRepository, times(1)).save(beneficiario);
    }
}