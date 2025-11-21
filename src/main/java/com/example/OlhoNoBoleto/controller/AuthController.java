package com.example.OlhoNoBoleto.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.OlhoNoBoleto.service.JwtUtil;
import com.example.OlhoNoBoleto.dto.user.LoginRequestDTO;
import com.example.OlhoNoBoleto.dto.user.UserRequestDTO;
import com.example.OlhoNoBoleto.dto.user.UserResponseDTO;
import com.example.OlhoNoBoleto.model.User;

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
    @Autowired
    private JwtUtil jwtService;

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
        try {
            System.out.println("🔐 Tentativa de login para: " + loginRequest.getEmail());

            User user = usuarioRepository.findByEmail(loginRequest.getEmail())
                    .orElseThrow(() -> {
                        System.out.println("❌ Usuário não encontrado: " + loginRequest.getEmail());
                        return new RuntimeException("Usuário ou senha inválidos");
                    });

            System.out.println("✅ Usuário encontrado: " + user.getEmail());

            if (!passwordEncoder.matches(loginRequest.getSenha(), user.getSenha())) {
                System.out.println("❌ Senha incorreta para: " + loginRequest.getEmail());
                return ResponseEntity.badRequest().body("Usuário ou senha inválidos");
            }

            System.out.println("✅ Credenciais válidas, gerando tokens...");

            // Gerar tokens
            String accessToken = jwtService.generateToken(user);

            System.out.println("✅ Tokens gerados com sucesso");

            // Retornar resposta com tokens
            Map<String, String> response = new HashMap<>();
            response.put("accessToken", accessToken);
            response.put("email", user.getEmail());
            response.put("role", user.getRole().name());
            response.put("nome", user.getNome());

            System.out.println("✅ Login realizado com sucesso para: " + user.getEmail());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.err.println("❌ ERRO no login: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body("Erro interno: " + e.getMessage());
        }
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