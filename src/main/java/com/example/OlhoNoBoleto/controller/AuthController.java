package com.example.OlhoNoBoleto.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.example.OlhoNoBoleto.dto.user.LoginRequestDTO;
import com.example.OlhoNoBoleto.dto.user.UserRequestDTO;
import com.example.OlhoNoBoleto.model.User;
import com.example.OlhoNoBoleto.service.AuthService;

public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<User> cadastrar(@RequestBody UserRequestDTO usuario) {
        return ResponseEntity.ok(authService.cadastrar(usuario));
    }

    @PostMapping("/login")
    public ResponseEntity<User> login(@RequestBody LoginRequestDTO login) {
        return ResponseEntity.ok(authService.login(login.getEmail(), login.getSenha()));
    }
}