package com.example.OlhoNoBoleto.controller;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.OlhoNoBoleto.dto.user.LoginRequestDTO;
// import com.example.OlhoNoBoleto.dto.user.LoginRequestDTO;
import com.example.OlhoNoBoleto.dto.user.UserRequestDTO;
import com.example.OlhoNoBoleto.model.User;
// import com.example.OlhoNoBoleto.service.AuthService;
import com.example.OlhoNoBoleto.repository.UsuarioRepository;

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
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid LoginRequestDTO usuario) {
        var user = usuarioRepository.findByEmail(usuario.getEmail());
        if (user.isPresent()) {
            boolean senhaCorreta = passwordEncoder.matches(usuario.getSenha(), user.get().getSenha());
            if (senhaCorreta) {
                return ResponseEntity.ok(user.get());
            } else {
                return ResponseEntity.status(401).body("Senha incorreta");
            }
        } else {
            return ResponseEntity.status(404).body("Usuário não encontrado");
        }
    }
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

    @PutMapping("atualizar/{id}")
    public ResponseEntity<?> atualizarUsuario(@RequestBody @Valid UserRequestDTO usuario, @PathVariable UUID id) {
        User updatedUser = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado com o ID: " + id));
        updatedUser.setNome(usuario.getNome());
        updatedUser.setEmail(usuario.getEmail());
        updatedUser.setSenha(passwordEncoder.encode(usuario.getSenha()));
        updatedUser.setRole(usuario.getRole() != null ? usuario.getRole() : updatedUser.getRole());
        usuarioRepository.save(updatedUser);

        return ResponseEntity.ok(updatedUser);
    }

    @GetMapping("/usuarios")
    public ResponseEntity mostrarUsuarios() {
        var allUsers = usuarioRepository.findAll();
        return ResponseEntity.ok(allUsers);
    }

}