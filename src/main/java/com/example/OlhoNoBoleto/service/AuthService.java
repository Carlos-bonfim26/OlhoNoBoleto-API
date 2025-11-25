package com.example.OlhoNoBoleto.service;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;

import com.example.OlhoNoBoleto.dto.user.UserRequestDTO;
import com.example.OlhoNoBoleto.dto.user.UserResponseDTO;
import com.example.OlhoNoBoleto.exceptions.BusinessException;
import com.example.OlhoNoBoleto.model.User;
import com.example.OlhoNoBoleto.repository.UsuarioRepository;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User cadastrar(UserRequestDTO usuario) {
        return null;
    }

    public User login(String email, String senha) {
        return null;
    }

    public UserResponseDTO atualizarUsuario(UUID id, UserRequestDTO usuario) {
        User user = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        if (usuario.getEmail() != null) {
            boolean emailExists = usuarioRepository.findByEmail(usuario.getEmail())
                    .filter(u -> !u.getId().equals(id))
                    .isPresent();

            if (emailExists) {
                throw new BusinessException("Email já está em uso por outro usuário", "EMAIL_ALREADY_EXISTS");
            }
        }
        user.setNome(usuario.getNome());
        user.setEmail(usuario.getEmail());

        if (usuario.getSenha() != null && !usuario.getSenha().isBlank()) {
            user.setSenha(passwordEncoder.encode(usuario.getSenha()));
        }

        if (usuario.getRole() != null) {
            user.setRole(usuario.getRole());
        }

        usuarioRepository.save(user);
        return new UserResponseDTO(user.getId(), user.getNome(), user.getEmail(), user.getRole());
    }
}