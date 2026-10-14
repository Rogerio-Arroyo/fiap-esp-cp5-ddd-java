package br.com.fiap.bank.plataforma.domain.model.vo;

import java.math.BigDecimal;

/**
 * Value Object que representa a fotografia da conta bancaria do cliente no
 * Core Bancario, obtida via CoreBancarioPort, usada pelo Motor de Credito
 * para validar movimentacoes e saldos durante a analise de risco.
 */
public record ContaClienteInfo(
        String statusConta,
        BigDecimal saldo,
        String agencia,
        String numeroConta
) {
    /** Representacao padrao usada quando o Core Bancario esta indisponivel ou o cliente nao possui conta. */
    public static ContaClienteInfo indisponivel() {
        return new ContaClienteInfo("INDISPONIVEL", BigDecimal.ZERO, "-", "-");
    }
}
