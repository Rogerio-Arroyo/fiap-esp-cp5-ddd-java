package br.com.fiap.bank.plataforma.infrastructure.gateway;

import br.com.fiap.bank.plataforma.domain.port.BureauCreditoPort;
import org.springframework.stereotype.Component;

/**
 * Adaptador simulado de integracao com o Bureau de Credito (ex.: Serasa/SPC).
 * Gera um score deterministico (400-950) derivado dos digitos do CPF.
 */
@Component
public class BureauCreditoAdapter implements BureauCreditoPort {

    @Override
    public int consultarScore(String cpf) {
        String cpfLimpo = cpf.replaceAll("\\D", "");
        if (cpfLimpo.isEmpty()) {
            return 500;
        }
        int soma = 0;
        for (char c : cpfLimpo.toCharArray()) {
            soma += Character.getNumericValue(c);
        }
        return 400 + (soma * 9) % 551;
    }
}
