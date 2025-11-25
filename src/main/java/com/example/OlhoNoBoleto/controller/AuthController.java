package com.example.OlhoNoBoleto.controller;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.example.OlhoNoBoleto.dto.user.LoginRequestDTO;
import com.example.OlhoNoBoleto.dto.user.UserRequestDTO;
import com.example.OlhoNoBoleto.dto.user.UserResponseDTO;
import com.example.OlhoNoBoleto.enums.Role;
import com.example.OlhoNoBoleto.model.User;
import com.example.OlhoNoBoleto.repository.UsuarioRepository;

import jakarta.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/cadastro")
    public ResponseEntity<?> cadastro(@RequestBody @Valid UserRequestDTO usuario) {
        if (usuarioRepository.findByEmail(usuario.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("Email já cadastrado");
        }

        User newUser = new User();
        newUser.setSenha(passwordEncoder.encode(usuario.getSenha()));
        newUser.setEmail(usuario.getEmail());
        newUser.setNome(usuario.getNome());
        newUser.setRole(usuario.getRole() != null ? usuario.getRole() : Role.ROLE_USER);

        User savedUser = usuarioRepository.save(newUser);

        UserResponseDTO responseDTO = new UserResponseDTO();
        responseDTO.setId(savedUser.getId());
        responseDTO.setNome(savedUser.getNome());
        responseDTO.setEmail(savedUser.getEmail());
        responseDTO.setRole(savedUser.getRole());

        return ResponseEntity.ok(responseDTO);
    }
    @PostMapping("/verificar-login")
    public ResponseEntity<?> verificarLogin(@RequestBody LoginRequestDTO loginRequest) {
        Optional<User> userOpt = usuarioRepository.findByEmail(loginRequest.getEmail());
        
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(401).body("Usuário não encontrado");
        }

        User user = userOpt.get();
        if (!passwordEncoder.matches(loginRequest.getSenha(), user.getSenha())) {
            return ResponseEntity.status(401).body("Senha incorreta");
        }

        UserResponseDTO responseDTO = new UserResponseDTO();
        responseDTO.setId(user.getId());
        responseDTO.setNome(user.getNome());
        responseDTO.setEmail(user.getEmail());
        responseDTO.setRole(user.getRole());

        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping("/usuario-atual")
    public ResponseEntity<?> getUsuarioAtual(org.springframework.security.core.Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).body("Usuário não autenticado");
        }

        String email = authentication.getName();
        User user = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        // Retornar DTO sem a senha
        UserResponseDTO userResponse = new UserResponseDTO();
        userResponse.setId(user.getId());
        userResponse.setNome(user.getNome());
        userResponse.setEmail(user.getEmail());
        userResponse.setRole(user.getRole());

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
        User userLogado = usuarioRepository.findByEmail(emailLogado)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (isAdmin || userLogado.getId().equals(id)) {
            User userToUpdate = usuarioRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

            userToUpdate.setNome(usuario.getNome());
            userToUpdate.setEmail(usuario.getEmail());
            if (usuario.getSenha() != null && !usuario.getSenha().trim().isEmpty()) {
                userToUpdate.setSenha(passwordEncoder.encode(usuario.getSenha()));
            }
            if (isAdmin && usuario.getRole() != null) {
                userToUpdate.setRole(usuario.getRole());
            }

            usuarioRepository.save(userToUpdate);
            return ResponseEntity.ok("Usuário atualizado com sucesso");
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