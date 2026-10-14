package br.com.fiap.bank.core.presentation.config;

import br.com.fiap.bank.core.application.dto.CriarClienteRequest;
import br.com.fiap.bank.core.application.dto.CriarContaRequest;
import br.com.fiap.bank.core.application.service.ClienteService;
import br.com.fiap.bank.core.application.service.ContaBancariaService;
import br.com.fiap.bank.core.domain.model.vo.TipoConta;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Carga inicial de dados de demonstracao do Core Bancario: cria os mesmos
 * correntistas simulados pela Plataforma de Credito, garantindo que o
 * ecossistema completo (incluindo o Internet Banking Mobile) encontre dados
 * coerentes ao consultar o Core Bancario logo na subida da aplicacao.
 */
@Component
public class CoreBancarioDataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(CoreBancarioDataInitializer.class);

    private final ClienteService clienteService;
    private final ContaBancariaService contaService;

    public CoreBancarioDataInitializer(ClienteService clienteService, ContaBancariaService contaService) {
        this.clienteService = clienteService;
        this.contaService = contaService;
    }

    @Override
    public void run(String... args) {
        try {
            // CPFs validos (digito verificador real, modulo 11), compartilhados com a
            // carga inicial da Plataforma de Credito e com o demo padrao do Internet Banking Mobile.
            criarClienteEConta("11122233396", "Ana Silva", "ana.silva@fiapbank.com.br",
                    LocalDate.of(1992, 4, 12), new BigDecimal("7500.00"), new BigDecimal("12500.00"));

            criarClienteEConta("98765432100", "Carlos Eduardo Oliveira", "carlos.oliveira@fiapbank.com.br",
                    LocalDate.of(1985, 9, 3), new BigDecimal("12000.00"), new BigDecimal("48000.00"));

            criarClienteEConta("44455566619", "Mariana Souza", "mariana.souza@fiapbank.com.br",
                    LocalDate.of(1998, 1, 20), new BigDecimal("4200.00"), new BigDecimal("3200.00"));

            log.info(">>> Core Bancario: dados iniciais de clientes e contas carregados com sucesso!");
        } catch (Exception e) {
            log.error(">>> Erro ao carregar dados iniciais do Core Bancario: {}", e.getMessage(), e);
        }
    }

    private void criarClienteEConta(String cpf, String nome, String email, LocalDate nascimento,
                                     BigDecimal renda, BigDecimal saldoInicial) {
        clienteService.criar(new CriarClienteRequest(cpf, nome, email, nascimento, renda));
        contaService.criar(new CriarContaRequest(cpf, "0001", TipoConta.CORRENTE, saldoInicial));
    }
}
