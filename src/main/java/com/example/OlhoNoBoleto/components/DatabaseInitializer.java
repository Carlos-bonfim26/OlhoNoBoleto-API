package com.example.OlhoNoBoleto.components;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@Component
public class DatabaseInitializer {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseInitializer.class);

    @Autowired
    private DataSource dataSource;

    private boolean databaseReady = false;

    @PostConstruct
    public void init() {
        // Inicia a inicialização do banco em background
        new Thread(() -> {
            try {
                logger.info("Iniciando inicialização do banco de dados em background...");
                // Tenta conectar ao banco
                try (Connection conn = dataSource.getConnection()) {
                    logger.info("Conexão com o banco de dados estabelecida com sucesso!");
                    databaseReady = true;
                } catch (SQLException e) {
                    logger.error("Erro ao conectar ao banco de dados: " + e.getMessage());
                    // Mesmo com erro, marque como pronto após um tempo? Ou tente novamente?
                    // Vamos tentar novamente após 30 segundos?
                    try {
                        Thread.sleep(30000);
                        try (Connection conn = dataSource.getConnection()) {
                            logger.info("Conexão com o banco de dados estabelecida após retry!");
                            databaseReady = true;
                        } catch (SQLException e2) {
                            logger.error("Erro ao conectar ao banco de dados no retry: " + e2.getMessage());
                        }
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                    }
                }
            } catch (Exception e) {
                logger.error("Erro durante a inicialização do banco de dados: " + e.getMessage());
            }
        }).start();
    }

    public boolean isDatabaseReady() {
        return databaseReady;
    }
}