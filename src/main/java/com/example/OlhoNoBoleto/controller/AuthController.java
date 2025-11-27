package com.example.OlhoNoBoleto.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.OlhoNoBoleto.dto.user.LoginRequestDTO;
import com.example.OlhoNoBoleto.dto.user.UserRequestDTO;
import com.example.OlhoNoBoleto.dto.user.UserResponseDTO;
import com.example.OlhoNoBoleto.model.User;
import com.example.OlhoNoBoleto.service.AuthService;
import com.example.OlhoNoBoleto.repository.UsuarioRepository;

import jakarta.validation.Valid;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @PostMapping("/cadastro")
    public ResponseEntity<?> cadastro(@RequestBody @Valid UserRequestDTO usuario) {
        try {
            User savedUser = authService.cadastrar(usuario);

            // Converte para ResponseDTO
            UserResponseDTO responseDTO = new UserResponseDTO();
            responseDTO.setId(savedUser.getId());
            responseDTO.setNome(savedUser.getNome());
            responseDTO.setEmail(savedUser.getEmail());
            responseDTO.setRole(savedUser.getRole());

            return ResponseEntity.ok(responseDTO);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/verificar-login")
    public ResponseEntity<?> verificarLogin(@RequestBody LoginRequestDTO loginRequest) {
        try {
            User user = authService.login(loginRequest.getEmail(), loginRequest.getSenha());

            UserResponseDTO responseDTO = new UserResponseDTO();
            responseDTO.setId(user.getId());
            responseDTO.setNome(user.getNome());
            responseDTO.setEmail(user.getEmail());
            responseDTO.setRole(user.getRole());

            return ResponseEntity.ok(responseDTO);
        } catch (Exception e) {
            return ResponseEntity.status(401).body(e.getMessage());
        }
    }

    @GetMapping("/usuario-atual")
    public ResponseEntity<?> getUsuarioAtual(org.springframework.security.core.Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).body("Usuário não autenticado");
        }

        String email = authentication.getName();
        Optional<User> user = usuarioRepository.findByEmail(email);

        if (user == null) {
            return ResponseEntity.status(404).body("Usuário não encontrado");
        }

        // Retornar DTO sem a senha
        UserResponseDTO userResponse = new UserResponseDTO();
        userResponse.setId(user.get().getId());
        userResponse.setNome(user.get().getNome());
        userResponse.setEmail(user.get().getEmail());
        userResponse.setRole(user.get().getRole());

        return ResponseEntity.ok(userResponse);
    }

    @PutMapping("/atualizar/{id}")
    public ResponseEntity<?> atualizarUsuario(@RequestBody @Valid UserRequestDTO usuario,
            @PathVariable UUID id,
            org.springframework.security.core.Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).body("Usuário não autenticado");
        }

        String emailLogado = authentication.getName();
        Optional<User> userLogadoOpt = usuarioRepository.findByEmail(emailLogado);

        if (userLogadoOpt.isEmpty()) {
            return ResponseEntity.status(404).body("Usuário logado não encontrado");
        }

        User userLogado = userLogadoOpt.get();
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (isAdmin || userLogado.getId().equals(id)) {
            try {
                UserResponseDTO updatedUser = authService.atualizarUsuario(id, usuario);
                return ResponseEntity.ok(updatedUser);
            } catch (Exception e) {
                return ResponseEntity.badRequest().body(e.getMessage());
            }
        }

        return ResponseEntity.status(403).body("Você não tem permissão para atualizar este usuário");
    }

    @GetMapping("/usuarios")
    public ResponseEntity<?> mostrarUsuarios(org.springframework.security.core.Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).body("Usuário não autenticado");
        }

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin) {
            return ResponseEntity.status(403).body("Acesso negado");
        }

        var allUsers = usuarioRepository.findAll();
        return ResponseEntity.ok(allUsers);
    }
}