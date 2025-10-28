package com.example.OlhoNoBoleto.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
@Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
            // 🔓 Desativa o CSRF, necessário para aceitar POST via Insomnia
            .csrf(csrf -> csrf.disable())
            
            // 🔓 Permite acesso livre a /cadastro e /login
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/cadastro", "/login", "/h2-console/**").permitAll()
                .anyRequest().permitAll() // (temporário) libera tudo para testes
            )
            
            //Desativa o formulário de login padrão
            .formLogin(form -> form.disable())
            
            // Desativa autenticação HTTP Basic
            .httpBasic(basic -> basic.disable());
        
        return http.build();
    }
}
