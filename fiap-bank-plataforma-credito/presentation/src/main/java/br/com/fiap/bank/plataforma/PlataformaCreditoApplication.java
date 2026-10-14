package br.com.fiap.bank.plataforma;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * Classe principal da aplicacao Plataforma de Credito.
 * <p>
 * Fisicamente reside no modulo presentation, mas e declarada no pacote raiz
 * br.com.fiap.bank.plataforma (em vez de br.com.fiap.bank.plataforma.presentation) para
 * que o component-scan padrao do Spring Boot alcance todas as camadas da
 * aplicacao (presentation, application, infrastructure) a partir deste pacote.
 */
@SpringBootApplication
@EnableFeignClients
public class PlataformaCreditoApplication {

    public static void main(String[] args) {
        SpringApplication.run(PlataformaCreditoApplication.class, args);
    }
}
