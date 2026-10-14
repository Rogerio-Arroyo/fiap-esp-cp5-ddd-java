package br.com.fiap.bank.core;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Classe principal da aplicacao Core Bancario.
 * <p>
 * Fisicamente reside no modulo presentation, mas e declarada no pacote raiz
 * br.com.fiap.bank.core (em vez de br.com.fiap.bank.core.presentation) para que o
 * component-scan padrao do Spring Boot alcance, a partir deste pacote, todas as
 * camadas da aplicacao (presentation, application, infrastructure), sem a
 * necessidade de componentes de dominio (que permanecem puros, sem anotacoes Spring).
 */
@SpringBootApplication
public class CoreBancarioApplication {

    public static void main(String[] args) {
        SpringApplication.run(CoreBancarioApplication.class, args);
    }
}
