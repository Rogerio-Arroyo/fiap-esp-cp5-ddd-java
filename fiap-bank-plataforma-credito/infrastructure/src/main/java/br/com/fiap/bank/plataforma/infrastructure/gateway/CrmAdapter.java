package br.com.fiap.bank.plataforma.infrastructure.gateway;

import br.com.fiap.bank.plataforma.domain.model.vo.HistoricoCrmInfo;
import br.com.fiap.bank.plataforma.domain.port.CrmPort;
import org.springframework.stereotype.Component;

/**
 * Adaptador simulado de integracao com o CRM corporativo do FIAP Bank,
 * consultado durante a orquestracao da analise de risco de credito (Secao 1
 * do Checkpoint: "historico do cliente no CRM corporativo").
 */
@Component
public class CrmAdapter implements CrmPort {

    @Override
    public HistoricoCrmInfo consultarHistorico(String cpf) {
        String cpfLimpo = cpf.replaceAll("\\D", "");
        int somaDigitos = cpfLimpo.chars().map(Character::getNumericValue).sum();
        boolean clienteFiel = somaDigitos % 3 == 0;
        int pontuacaoRelacionamento = 50 + (somaDigitos * 7) % 51;
        return new HistoricoCrmInfo(clienteFiel, pontuacaoRelacionamento);
    }
}
