package com.example.OlhoNoBoleto.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.OlhoNoBoleto.model.User;

public interface UsuarioRepository extends JpaRepository<User, UUID> {
    List<User> findByEmail(String email);

}
