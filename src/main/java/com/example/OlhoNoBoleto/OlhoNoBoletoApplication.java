package com.example.OlhoNoBoleto;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;


@SpringBootApplication
public class OlhoNoBoletoApplication {

	public static void main(String[] args) {  
        SpringApplication app = new SpringApplication(OlhoNoBoletoApplication.class);
        app.setLazyInitialization(true);
        app.setLogStartupInfo(false);
	}

}
