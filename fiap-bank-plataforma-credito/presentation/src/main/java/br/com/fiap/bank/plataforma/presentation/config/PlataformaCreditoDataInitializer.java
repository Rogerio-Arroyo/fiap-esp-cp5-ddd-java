package br.com.fiap.bank.plataforma.presentation.config;

import br.com.fiap.bank.plataforma.application.dto.SolicitarCreditoRequest;
import br.com.fiap.bank.plataforma.application.service.SolicitacaoCreditoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Carga inicial de solicitacoes de credito de demonstracao, usando os MESMOS
 * CPFs validos dos correntistas semeados no Core Bancario
 * (CoreBancarioDataInitializer), garantindo coerencia ponta a ponta do
 * ecossistema demonstrado (Mobile -> Plataforma -> Core Bancario).
 */
@Component
public class PlataformaCreditoDataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(PlataformaCreditoDataInitializer.class);

    private final SolicitacaoCreditoService solicitacaoService;

    public PlataformaCreditoDataInitializer(SolicitacaoCreditoService solicitacaoService) {
        this.solicitacaoService = solicitacaoService;
    }

    @Override
    public void run(String... args) {
        try {
            // Ana Silva: parcela (625.00) < 30% da renda (2250.00) e score >= 500 -> aprovacao automatica
            solicitacaoService.solicitar(new SolicitarCreditoRequest(
                    "11122233396", "Ana Silva", "EMPRESTIMO_PESSOAL",
                    new BigDecimal("15000.00"), 24, new BigDecimal("7500.00")));

            // Carlos Eduardo Oliveira: parcela (1944.44) < 30% da renda (3600.00) -> aprovacao automatica
            solicitacaoService.solicitar(new SolicitarCreditoRequest(
                    "98765432100", "Carlos Eduardo Oliveira", "CREDITO_IMOBILIARIO",
                    new BigDecimal("350000.00"), 180, new BigDecimal("12000.00")));

            // Mariana Souza: CPF terminado em 9 -> demonstra a reprovacao automatica por restricao no SCR
            solicitacaoService.solicitar(new SolicitarCreditoRequest(
                    "44455566619", "Mariana Souza", "CREDITO_AUTOMOTIVO",
                    new BigDecimal("45000.00"), 48, new BigDecimal("4200.00")));

            log.info(">>> Plataforma de Credito: dados iniciais carregados com sucesso!");
        } catch (Exception e) {
            log.error(">>> Erro ao carregar dados iniciais da Plataforma de Credito: {}", e.getMessage(), e);
        }
    }
}
