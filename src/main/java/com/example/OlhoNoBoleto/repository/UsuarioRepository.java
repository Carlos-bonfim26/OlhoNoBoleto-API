package com.example.OlhoNoBoleto.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.OlhoNoBoleto.model.User;

public interface UsuarioRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);

}
