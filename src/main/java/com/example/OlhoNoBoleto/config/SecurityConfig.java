package com.example.OlhoNoBoleto.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .anyRequest().permitAll() // 🔥 PERMITE TUDO - SEM AUTENTICAÇÃO
            );
             return http.build();
        }
    // public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    //     http
    //         .csrf(csrf -> csrf.disable())
    //         .authorizeHttpRequests(auth -> auth
    //             .requestMatchers("/auth/cadastro", "/auth/login", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
    //             .requestMatchers(HttpMethod.GET, "/auth/usuarios/**").hasRole("ADMIN")
    //             .anyRequest().authenticated())
    //         .formLogin(form -> form
    //             .loginProcessingUrl("/auth/login")
    //             .usernameParameter("email")
    //             .passwordParameter("senha")
    //             .successHandler((request, response, authentication) -> {
    //                 response.setStatus(200);
    //                 response.getWriter().write("{\"message\": \"Login bem-sucedido\"}");
    //             })
    //             .failureHandler((request, response, exception) -> {
    //                 response.setStatus(401);
    //                 response.getWriter().write("{\"message\": \"Credenciais inválidas\"}");
    //             })
    //             .permitAll()
    //         )
    //         .logout(logout -> logout
    //             .logoutUrl("/auth/logout")
    //             .logoutSuccessHandler((request, response, authentication) -> {
    //                 response.setStatus(200);
    //                 response.getWriter().write("{\"message\": \"Logout realizado\"}");
    //             })
    //             .deleteCookies("JSESSIONID")
    //             .invalidateHttpSession(true)
    //             .permitAll()
    //         );

    //     return http.build();
    // }

    // @Bean
    // public PasswordEncoder passwordEncoder() {
    //     return new BCryptPasswordEncoder();
    // }
}
