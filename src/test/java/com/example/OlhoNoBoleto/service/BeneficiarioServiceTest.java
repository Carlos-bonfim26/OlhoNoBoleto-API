package com.example.OlhoNoBoleto.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.example.OlhoNoBoleto.model.Beneficiario;
import com.example.OlhoNoBoleto.repository.BeneficiarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BeneficiarioServiceTest {

    @Mock
    private BeneficiarioRepository beneficiarioRepository;

    @InjectMocks
    private BeneficiarioService beneficiarioService;

    private Beneficiario beneficiarioExistente;
    private String document;
    private String nome;
    private String banco;
    private String agencia;

    @BeforeEach
    void setUp() {
        document = "12345678900";
        nome = "Empresa Teste LTDA";
        banco = "001";
        agencia = "0001";

        beneficiarioExistente = new Beneficiario();
        beneficiarioExistente.setId(UUID.randomUUID());
        beneficiarioExistente.setDocument(document);
        beneficiarioExistente.setNome("Nome Antigo");
        beneficiarioExistente.setBanco("002");
        beneficiarioExistente.setAgencia("0002");
        beneficiarioExistente.setTotalQueixas(5);
    }

    @Test
    void buscarPorCnpjCpf_QuandoEncontraBeneficiarios_DeveRetornarLista() {
        // Arrange
        List<Beneficiario> beneficiarios = Arrays.asList(beneficiarioExistente);
        when(beneficiarioRepository.findByDocument(document)).thenReturn(beneficiarios);

        // Act
        List<Beneficiario> resultado = beneficiarioService.buscarPorCnpjCpf(document);

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(beneficiarioExistente, resultado.get(0));
        verify(beneficiarioRepository).findByDocument(document);
    }

    @Test
    void buscarPorCnpjCpf_QuandoNaoEncontraBeneficiarios_DeveRetornarListaVazia() {
        // Arrange
        when(beneficiarioRepository.findByDocument(document)).thenReturn(Arrays.asList());

        // Act
        List<Beneficiario> resultado = beneficiarioService.buscarPorCnpjCpf(document);

        // Assert
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        verify(beneficiarioRepository).findByDocument(document);
    }

    @Test
    void buscarOuCriarBeneficiario_QuandoBeneficiarioExiste_DeveRetornarBeneficiarioExistente() {
        // Arrange
        List<Beneficiario> beneficiarios = Arrays.asList(beneficiarioExistente);
        when(beneficiarioRepository.findByDocument(document)).thenReturn(beneficiarios);

        // Act
        Optional<Beneficiario> resultado = beneficiarioService.buscarOuCriarBeneficiario(document, nome, banco, agencia);

        // Assert
        assertTrue(resultado.isPresent());
        assertEquals(beneficiarioExistente, resultado.get());
        // Verifica que NÃO foi chamado o save para criar novo beneficiário
        verify(beneficiarioRepository, never()).save(any(Beneficiario.class));
        verify(beneficiarioRepository).findByDocument(document);
    }

    @Test
    void buscarOuCriarBeneficiario_QuandoBeneficiarioNaoExiste_DeveCriarNovoBeneficiario() {
        // Arrange
        when(beneficiarioRepository.findByDocument(document)).thenReturn(Arrays.asList());
        when(beneficiarioRepository.save(any(Beneficiario.class))).thenAnswer(invocation -> {
            Beneficiario beneficiarioSalvo = invocation.getArgument(0);
            beneficiarioSalvo.setId(UUID.randomUUID()); // Simula a geração do ID
            return beneficiarioSalvo;
        });

        // Act
        Optional<Beneficiario> resultado = beneficiarioService.buscarOuCriarBeneficiario(document, nome, banco, agencia);

        // Assert
        assertTrue(resultado.isPresent());
        Beneficiario novoBeneficiario = resultado.get();
        
        assertEquals(document, novoBeneficiario.getDocument());
        assertEquals(nome, novoBeneficiario.getNome());
        assertEquals(banco, novoBeneficiario.getBanco());
        assertEquals(agencia, novoBeneficiario.getAgencia());
        assertEquals(0, novoBeneficiario.getTotalQueixas());
        
        verify(beneficiarioRepository).findByDocument(document);
        verify(beneficiarioRepository).save(any(Beneficiario.class));
    }

    @Test
    void buscarOuCriarBeneficiario_QuandoExistemMultiplosBeneficiariosComMesmoDocumento_DeveRetornarPrimeiro() {
        // Arrange
        Beneficiario outroBeneficiario = new Beneficiario();
        outroBeneficiario.setId(UUID.randomUUID());
        outroBeneficiario.setDocument(document);
        outroBeneficiario.setNome("Outra Empresa");
        
        List<Beneficiario> multiplosBeneficiarios = Arrays.asList(beneficiarioExistente, outroBeneficiario);
        when(beneficiarioRepository.findByDocument(document)).thenReturn(multiplosBeneficiarios);

        // Act
        Optional<Beneficiario> resultado = beneficiarioService.buscarOuCriarBeneficiario(document, nome, banco, agencia);

        // Assert
        assertTrue(resultado.isPresent());
        assertEquals(beneficiarioExistente, resultado.get());
        verify(beneficiarioRepository, never()).save(any(Beneficiario.class));
    }

    @Test
    void buscarOuCriarBeneficiario_QuandoNaoExisteBeneficiario_DeveSalvarComDadosCorretos() {
        // Arrange
        when(beneficiarioRepository.findByDocument(document)).thenReturn(Arrays.asList());
        when(beneficiarioRepository.save(any(Beneficiario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Optional<Beneficiario> resultado = beneficiarioService.buscarOuCriarBeneficiario(document, nome, banco, agencia);

        // Assert
        assertTrue(resultado.isPresent());
        Beneficiario novoBeneficiario = resultado.get();
        
        // Verifica se todos os campos foram setados corretamente
        assertEquals(document, novoBeneficiario.getDocument());
        assertEquals(nome, novoBeneficiario.getNome());
        assertEquals(banco, novoBeneficiario.getBanco());
        assertEquals(agencia, novoBeneficiario.getAgencia());
        assertEquals(0, novoBeneficiario.getTotalQueixas());
    }

    @Test
    void buscarOuCriarBeneficiario_QuandoNaoExisteBeneficiario_DeveChamarSaveApenasUmaVez() {
        // Arrange
        when(beneficiarioRepository.findByDocument(document)).thenReturn(Arrays.asList());
        when(beneficiarioRepository.save(any(Beneficiario.class))).thenReturn(new Beneficiario());

        // Act
        beneficiarioService.buscarOuCriarBeneficiario(document, nome, banco, agencia);

        // Assert
        verify(beneficiarioRepository, times(1)).save(any(Beneficiario.class));
    }

    @Test
    void buscarOuCriarBeneficiario_QuandoExisteBeneficiario_NaoDeveChamarSave() {
        // Arrange
        List<Beneficiario> beneficiarios = Arrays.asList(beneficiarioExistente);
        when(beneficiarioRepository.findByDocument(document)).thenReturn(beneficiarios);

        // Act
        beneficiarioService.buscarOuCriarBeneficiario(document, nome, banco, agencia);

        // Assert
        verify(beneficiarioRepository, never()).save(any(Beneficiario.class));
    }
}