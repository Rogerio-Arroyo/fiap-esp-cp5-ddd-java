package br.com.fiap.bank.plataforma.infrastructure.gateway;

import br.com.fiap.bank.plataforma.domain.port.ScrPort;
import org.springframework.stereotype.Component;

/**
 * Adaptador simulado de integracao com o Sistema do Banco Central (SCR).
 * Em um ambiente real, este adaptador consumiria a API oficial do SCR; aqui,
 * replica deterministicamente a regra de negocio original para fins didaticos:
 * CPFs terminados em 9 possuem restricao/divida vencida registrada.
 */
@Component
public class ScrAdapter implements ScrPort {

    @Override
    public boolean consultarRestricoes(String cpf) {
        String cpfLimpo = cpf.replaceAll("\\D", "");
        return cpfLimpo.endsWith("9");
    }
}
