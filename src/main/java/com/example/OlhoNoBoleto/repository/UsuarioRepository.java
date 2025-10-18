package com.example.OlhoNoBoleto.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.OlhoNoBoleto.model.User;

public interface UsuarioRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

}
