package com.example.OlhoNoBoleto.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Optional;
import java.util.UUID;

import com.example.OlhoNoBoleto.dto.user.UserRequestDTO;
import com.example.OlhoNoBoleto.dto.user.UserResponseDTO;
import com.example.OlhoNoBoleto.model.User;
import com.example.OlhoNoBoleto.enums.Role;
import com.example.OlhoNoBoleto.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    private User user;
    private UserRequestDTO userRequestDTO;
    private UUID userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        
        user = new User();
        user.setId(userId);
        user.setNome("João Silva");
        user.setEmail("joao@email.com");
        user.setSenha("senha123");
        user.setRole(Role.ROLE_USER);

        userRequestDTO = new UserRequestDTO();
        userRequestDTO.setNome("João Silva Atualizado");
        userRequestDTO.setEmail("joao.atualizado@email.com");
        userRequestDTO.setSenha("novaSenha123");
        userRequestDTO.setRole(Role.ROLE_ADMIN);
    }

    @Test
    void cadastrar_DeveRetornarNull() {
        User result = authService.cadastrar(userRequestDTO);

        assertNull(result);
    }

    @Test
    void login_DeveRetornarNull() {
        User result = authService.login("email@teste.com", "senha");

        assertNull(result);
    }

    @Test
    void atualizarUsuario_QuandoUsuarioExiste_DeveAtualizarUsuario() {
        when(usuarioRepository.findById(userId)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode("novaSenha123")).thenReturn("senhaCriptografada");
        when(usuarioRepository.save(any(User.class))).thenReturn(user);

        UserResponseDTO result = authService.atualizarUsuario(userId, userRequestDTO);

        assertNotNull(result);
        assertEquals(userId, result.getId());
        assertEquals("João Silva Atualizado", result.getNome());
        assertEquals("joao.atualizado@email.com", result.getEmail());
        assertEquals(Role.ROLE_ADMIN, result.getRole());

        verify(usuarioRepository).findById(userId);
        verify(passwordEncoder).encode("novaSenha123");
        verify(usuarioRepository).save(user);
        
        assertEquals("João Silva Atualizado", user.getNome());
        assertEquals("joao.atualizado@email.com", user.getEmail());
        assertEquals("senhaCriptografada", user.getSenha());
        assertEquals(Role.ROLE_ADMIN, user.getRole());
    }

    @Test
    void atualizarUsuario_QuandoUsuarioNaoExiste_DeveLancarExcecao() {
        when(usuarioRepository.findById(userId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> authService.atualizarUsuario(userId, userRequestDTO));
        
        assertEquals("Usuário não encontrado", exception.getMessage());
        
        verify(usuarioRepository).findById(userId);
        verify(usuarioRepository, never()).save(any(User.class));
    }

    @Test
    void atualizarUsuario_QuandoSenhaENula_DeveManterSenhaOriginal() {
        userRequestDTO.setSenha(null);
        when(usuarioRepository.findById(userId)).thenReturn(Optional.of(user));
        when(usuarioRepository.save(any(User.class))).thenReturn(user);

        String senhaOriginal = user.getSenha();

        UserResponseDTO result = authService.atualizarUsuario(userId, userRequestDTO);

        assertNotNull(result);
        assertEquals(senhaOriginal, user.getSenha());
        
        verify(passwordEncoder, never()).encode(anyString());
        verify(usuarioRepository).save(user);
    }

    @Test
    void atualizarUsuario_QuandoSenhaEVazia_DeveManterSenhaOriginal() {
        userRequestDTO.setSenha("");
        when(usuarioRepository.findById(userId)).thenReturn(Optional.of(user));
        when(usuarioRepository.save(any(User.class))).thenReturn(user);

        String senhaOriginal = user.getSenha();

        UserResponseDTO result = authService.atualizarUsuario(userId, userRequestDTO);

        assertNotNull(result);
        assertEquals(senhaOriginal, user.getSenha());
        
        verify(passwordEncoder, never()).encode(anyString());
        verify(usuarioRepository).save(user);
    }

    @Test
    void atualizarUsuario_QuandoSenhaEEmBranco_DeveManterSenhaOriginal() {
        userRequestDTO.setSenha("   ");
        when(usuarioRepository.findById(userId)).thenReturn(Optional.of(user));
        when(usuarioRepository.save(any(User.class))).thenReturn(user);

        String senhaOriginal = user.getSenha();

        UserResponseDTO result = authService.atualizarUsuario(userId, userRequestDTO);

        assertNotNull(result);
        assertEquals(senhaOriginal, user.getSenha());
        
        verify(passwordEncoder, never()).encode(anyString());
        verify(usuarioRepository).save(user);
    }

    @Test
    void atualizarUsuario_QuandoRoleENula_DeveManherRoleOriginal() {
        userRequestDTO.setRole(null);
        when(usuarioRepository.findById(userId)).thenReturn(Optional.of(user));
        when(usuarioRepository.save(any(User.class))).thenReturn(user);

        Role roleOriginal = user.getRole();

        UserResponseDTO result = authService.atualizarUsuario(userId, userRequestDTO);

        assertNotNull(result);
        assertEquals(roleOriginal, user.getRole());
        verify(usuarioRepository).save(user);
    }

    @Test
    void atualizarUsuario_QuandoApenasNomeEEmailSaoAtualizados_DeveAtualizarCorretamente() {
        UserRequestDTO requestParcial = new UserRequestDTO();
        requestParcial.setNome("Novo Nome");
        requestParcial.setEmail("novo@email.com");

        when(usuarioRepository.findById(userId)).thenReturn(Optional.of(user));
        when(usuarioRepository.save(any(User.class))).thenReturn(user);

        String senhaOriginal = user.getSenha();
        Role roleOriginal = user.getRole();

        UserResponseDTO result = authService.atualizarUsuario(userId, requestParcial);

        assertNotNull(result);
        assertEquals("Novo Nome", user.getNome());
        assertEquals("novo@email.com", user.getEmail());
        assertEquals(senhaOriginal, user.getSenha());
        assertEquals(roleOriginal, user.getRole());
        
        verify(passwordEncoder, never()).encode(anyString());
        verify(usuarioRepository).save(user);
    }
}
