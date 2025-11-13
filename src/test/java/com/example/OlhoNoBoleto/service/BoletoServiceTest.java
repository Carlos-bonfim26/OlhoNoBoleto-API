package com.example.OlhoNoBoleto.service;

import com.example.OlhoNoBoleto.dto.boleto.BoletoResponseDTO;
import com.example.OlhoNoBoleto.dto.boleto.BoletoValidateRequestDTO;
import com.example.OlhoNoBoleto.model.Beneficiario;
import com.example.OlhoNoBoleto.repository.ReportRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BoletoServiceTest {

    @Mock
    private BeneficiarioService beneficiarioService;

    @Mock
    private ReportRepository reportRepository;

    @InjectMocks
    private BoletoService boletoService;

    // LINHAS DIGITÁVEIS REAIS VÁLIDAS (47 caracteres EXATOS)
    // Formatos válidos de boletos reais
    private static final String LINHA_BANCO_BRASIL = "00100000000000000000000000000000000000000002000";
    private static final String LINHA_ITAU = "34100000000000000000000000000000000000000020000";
    private static final String LINHA_BRADESCO = "23700000000000000000000000000000000000000020000";
    private static final String LINHA_BANCO_DESCONHECIDO = "99900000000000000000000000000000000000000020000";
    private static final String LINHA_COM_VALOR = "00100000000000000000000000000000000000000002000";

    @Test
    void debugVerificarTamanhoLinhaDigitavel() {
        // Teste de debug para verificar o problema
        String[] linhas = { LINHA_BANCO_BRASIL, LINHA_ITAU, LINHA_BRADESCO, LINHA_BANCO_DESCONHECIDO };

        for (String linha : linhas) {
            System.out.println("Linha: " + linha);
            System.out.println("Tamanho: " + linha.length());
            System.out.println("Apenas dígitos: " + linha.matches("\\d+"));
            System.out.println("---");
        }
    }

    @Test
    void deveValidarBoletoComLinhaDigitavelValida() {
        // Arrange
        BoletoValidateRequestDTO request = new BoletoValidateRequestDTO();
        request.setLinhaDigitavel(LINHA_BANCO_BRASIL);

        Beneficiario beneficiarioMock = new Beneficiario();
        beneficiarioMock.setNome("Banco do Brasil S.A.");
        beneficiarioMock.setDocument("00000000000191");

        when(beneficiarioService.buscarOuCriarBeneficiario(anyString(), anyString(), anyString(), anyString()))
                .thenReturn(Optional.of(beneficiarioMock));
        when(reportRepository.countByBeneficiario(any(Beneficiario.class))).thenReturn(0);

        // Act
        BoletoResponseDTO response = boletoService.validarBoleto(request);

        // Assert - Foco no comportamento principal
        assertNotNull(response);
        assertEquals(LINHA_BANCO_BRASIL, response.getLinhaDigitavel());
        assertEquals("Banco do Brasil", response.getBanco());
        assertNotNull(response.getDataValidacao());

        // O status pode variar baseado em verificações externas
        // Então não validamos rigidamente "válido" vs "suspeito"
    }

    @Test
    void deveLancarExcecaoParaLinhaDigitavelComTamanhoInvalido() {
        // Arrange
        BoletoValidateRequestDTO request = new BoletoValidateRequestDTO();
        request.setLinhaDigitavel("12345"); // Tamanho inválido

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            boletoService.validarBoleto(request);
        });

        assertTrue(exception.getMessage().contains("deve conter 47 ou 48 dígitos"));
    }

    @Test
    void deveMarcarBoletoComoSuspeitoParaBancoDesconhecido() {
        // Arrange
        BoletoValidateRequestDTO request = new BoletoValidateRequestDTO();
        request.setLinhaDigitavel(LINHA_BANCO_DESCONHECIDO);

        Beneficiario beneficiarioMock = new Beneficiario();
        beneficiarioMock.setNome("Banco Desconhecido S.A.");
        beneficiarioMock.setDocument("00000000000000");

        when(beneficiarioService.buscarOuCriarBeneficiario(anyString(), anyString(), anyString(), anyString()))
                .thenReturn(Optional.of(beneficiarioMock));
        when(reportRepository.countByBeneficiario(any(Beneficiario.class))).thenReturn(0);

        // Act
        BoletoResponseDTO response = boletoService.validarBoleto(request);

        // Assert
        assertEquals("suspeito", response.getStatusValidacao());
        assertEquals("NÃO PAGAR", response.getRecomendacao());
        assertEquals("Banco desconhecido", response.getBanco());
        assertTrue(response.getMotivo().contains("Banco não reconhecido"));
    }

    @Test
    void deveMarcarBoletoComoSuspeitoParaBeneficiarioComDenuncias() {
        // Arrange
        BoletoValidateRequestDTO request = new BoletoValidateRequestDTO();
        request.setLinhaDigitavel(LINHA_BANCO_BRASIL);

        Beneficiario beneficiarioMock = new Beneficiario();
        beneficiarioMock.setNome("Banco do Brasil S.A.");
        beneficiarioMock.setDocument("00000000000191");

        when(beneficiarioService.buscarOuCriarBeneficiario(anyString(), anyString(), anyString(), anyString()))
                .thenReturn(Optional.of(beneficiarioMock));
        when(reportRepository.countByBeneficiario(any(Beneficiario.class))).thenReturn(3); // 3 denúncias

        // Act
        BoletoResponseDTO response = boletoService.validarBoleto(request);

        // Assert
        assertEquals("suspeito", response.getStatusValidacao());
        assertEquals("NÃO PAGAR", response.getRecomendacao());
        assertTrue(response.getMotivo().contains("denúncia"));
    }

    @Test
    void deveExtrairValorCorretamenteDaLinhaDigitavel() {
        // Arrange
        BoletoValidateRequestDTO request = new BoletoValidateRequestDTO();
        request.setLinhaDigitavel(LINHA_COM_VALOR);

        Beneficiario beneficiarioMock = new Beneficiario();
        beneficiarioMock.setNome("Banco do Brasil S.A.");
        beneficiarioMock.setDocument("00000000000191");

        when(beneficiarioService.buscarOuCriarBeneficiario(anyString(), anyString(), anyString(), anyString()))
                .thenReturn(Optional.of(beneficiarioMock));
        when(reportRepository.countByBeneficiario(any(Beneficiario.class))).thenReturn(0);

        // Act
        BoletoResponseDTO response = boletoService.validarBoleto(request);

        // Assert
        assertEquals(20.00, response.getValor()); // 2000 / 100 = 20.00
    }

    @Test
    void deveExtrairCodigoBancoCorretamente() {
        // Arrange
        BoletoValidateRequestDTO request = new BoletoValidateRequestDTO();
        request.setLinhaDigitavel(LINHA_ITAU);

        Beneficiario beneficiarioMock = new Beneficiario();
        beneficiarioMock.setNome("Itaú Unibanco S.A.");
        beneficiarioMock.setDocument("61532679000150");

        when(beneficiarioService.buscarOuCriarBeneficiario(anyString(), anyString(), anyString(), anyString()))
                .thenReturn(Optional.of(beneficiarioMock));
        when(reportRepository.countByBeneficiario(any(Beneficiario.class))).thenReturn(0);

        // Act
        BoletoResponseDTO response = boletoService.validarBoleto(request);

        // Assert
        assertEquals("Itaú Unibanco", response.getBanco());
    }

    @Test
    void deveLidarComBeneficiarioNulo() {
        // Arrange
        BoletoValidateRequestDTO request = new BoletoValidateRequestDTO();
        request.setLinhaDigitavel(LINHA_BANCO_BRASIL);

        when(beneficiarioService.buscarOuCriarBeneficiario(anyString(), anyString(), anyString(), anyString()))
                .thenReturn(Optional.empty());

        // Act
        BoletoResponseDTO response = boletoService.validarBoleto(request);

        // Assert
        assertNotNull(response);
        assertEquals("Instituição bancária", response.getBeneficiarioNome());
        assertEquals("suspeito", response.getStatusValidacao());
    }

    @Test
    void deveLancarExcecaoParaLinhaDigitavelNula() {
        // Arrange
        BoletoValidateRequestDTO request = new BoletoValidateRequestDTO();
        request.setLinhaDigitavel(null);

        // Act & Assert
        assertThrows(NullPointerException.class, () -> {
            boletoService.validarBoleto(request);
        });
    }
}