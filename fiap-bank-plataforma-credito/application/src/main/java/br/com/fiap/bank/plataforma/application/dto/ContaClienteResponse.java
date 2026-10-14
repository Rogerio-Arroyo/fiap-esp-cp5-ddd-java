package br.com.fiap.bank.plataforma.application.dto;

import br.com.fiap.bank.plataforma.domain.model.vo.ContaClienteInfo;

import java.math.BigDecimal;

/**
 * Representacao de saida da consulta (proxy) de conta do cliente no Core Bancario,
 * mantendo o mesmo formato de campos consumido pelo Internet Banking Mobile.
 */
public record ContaClienteResponse(
        String statusConta,
        BigDecimal saldo,
        String agencia,
        String numeroConta
) {
    public static ContaClienteResponse fromDomain(ContaClienteInfo info) {
        return new ContaClienteResponse(info.statusConta(), info.saldo(), info.agencia(), info.numeroConta());
    }
}
