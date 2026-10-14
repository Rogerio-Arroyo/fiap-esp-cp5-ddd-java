package br.com.fiap.bank.core.application.dto;

import br.com.fiap.bank.core.domain.model.Cliente;
import br.com.fiap.bank.core.domain.model.ContaBancaria;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/** Representacao de saida (Application layer) de uma Conta Bancaria, com dados do titular agregados para conveniencia do consumidor. */
public record ContaResponse(
        UUID id,
        String numero,
        String agencia,
        String tipoConta,
        BigDecimal saldo,
        String status,
        LocalDate dataAbertura,
        String cpfCliente,
        String nomeCliente
) {
    public static ContaResponse fromDomain(ContaBancaria conta, Cliente titular) {
        return new ContaResponse(
                conta.getId(),
                conta.getNumero(),
                conta.getAgencia(),
                conta.getTipoConta().name(),
                conta.getSaldo().valor(),
                conta.getStatus().name(),
                conta.getDataAbertura(),
                titular.getCpf().valor(),
                titular.getNome()
        );
    }
}
