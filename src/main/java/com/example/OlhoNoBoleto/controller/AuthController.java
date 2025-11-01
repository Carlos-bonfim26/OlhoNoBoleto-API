package com.example.OlhoNoBoleto.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.OlhoNoBoleto.dto.user.LoginRequestDTO;
import com.example.OlhoNoBoleto.dto.user.UserRequestDTO;
import com.example.OlhoNoBoleto.dto.user.UserResponseDTO;
import com.example.OlhoNoBoleto.model.User;
// import com.example.OlhoNoBoleto.service.AuthService;
import com.example.OlhoNoBoleto.repository.UsuarioRepository;
import com.example.OlhoNoBoleto.service.AuthService;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AuthService authService;

    @PostMapping("/cadastro")
    public ResponseEntity<?> cadastro(@RequestBody @Valid UserRequestDTO usuario) {
        if (usuarioRepository.findByEmail(usuario.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("Email já cadastrado");
        }
        User newUser = new User();
        newUser.setSenha(passwordEncoder.encode(usuario.getSenha()));
        newUser.setEmail(usuario.getEmail());
        newUser.setNome(usuario.getNome());
        newUser.setRole(usuario.getRole());
        usuarioRepository.save(newUser);
        System.out.println(newUser);
        return ResponseEntity.ok(newUser);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid LoginRequestDTO loginRequest) {
        User user = usuarioRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new RuntimeException("Usuário ou senha inválidos"));
        if (!passwordEncoder.matches(loginRequest.getSenha(), user.getSenha())) {
            return ResponseEntity.badRequest().body("Usuário ou senha inválidos");
        }
        return ResponseEntity.ok("Login bem-sucedido para o usuário: " + loginRequest.getEmail());
    }

    @PutMapping("atualizar/{id}")
    public ResponseEntity<?> atualizarUsuario(@RequestBody @Valid UserRequestDTO usuario, @PathVariable UUID id,
            Authentication authenticator) {
        UserDetails userLogado = (UserDetails) authenticator.getPrincipal(); 
        User userDoBanco = usuarioRepository.findByEmail(userLogado.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        boolean isAdmin = userLogado.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (isAdmin || userDoBanco.getId().equals(id)) {
            UserResponseDTO updated = authService.atualizarUsuario(id, usuario);
            return ResponseEntity.ok(updated);
        }

        return ResponseEntity.status(403).body("Você não tem permissão para atualizar este usuário.");
    }

    @GetMapping("/usuarios")
    public ResponseEntity mostrarUsuarios() {
        var allUsers = usuarioRepository.findAll();
        return ResponseEntity.ok(allUsers);
    }

}