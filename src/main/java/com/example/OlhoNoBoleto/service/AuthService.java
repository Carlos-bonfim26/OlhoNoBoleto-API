package com.example.OlhoNoBoleto.service;

import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.OlhoNoBoleto.dto.user.UserRequestDTO;
import com.example.OlhoNoBoleto.dto.user.UserResponseDTO;
import com.example.OlhoNoBoleto.exceptions.BusinessException;
import com.example.OlhoNoBoleto.model.User;
import com.example.OlhoNoBoleto.repository.UsuarioRepository;

import org.springframework.security.crypto.password.PasswordEncoder;

@Service
public class AuthService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User cadastrar(UserRequestDTO usuarioDTO) {
        // Verifica se o email já existe
        Optional<User> existingUser = usuarioRepository.findByEmail(usuarioDTO.getEmail());
        if (existingUser.isPresent()) {
            throw new BusinessException("Email já cadastrado", "EMAIL_ALREADY_EXISTS");
        }

        // Cria novo usuário
        User user = new User();
        user.setNome(usuarioDTO.getNome());
        user.setEmail(usuarioDTO.getEmail());
        user.setSenha(passwordEncoder.encode(usuarioDTO.getSenha()));
        user.setRole(usuarioDTO.getRole() != null ? usuarioDTO.getRole() : com.example.OlhoNoBoleto.enums.Role.ROLE_USER);

        return usuarioRepository.save(user);
    }

    public User login(String email, String senha) {
        // Busca usuário pelo email
        Optional<User> userOpt = usuarioRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            throw new UsernameNotFoundException("Usuário não encontrado");
        }

        User user = userOpt.get();

        // Verifica a senha
        if (!passwordEncoder.matches(senha, user.getSenha())) {
            throw new BusinessException("Senha incorreta", "INVALID_PASSWORD");
        }

        return user;
    }

    public UserResponseDTO atualizarUsuario(UUID id, UserRequestDTO usuario) {
        User user = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        if (usuario.getEmail() != null) {
            // Verifica se o email já está em uso por outro usuário
            Optional<User> existingUser = usuarioRepository.findByEmail(usuario.getEmail());
            if (existingUser.isPresent() && !existingUser.get().getId().equals(id)) {
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