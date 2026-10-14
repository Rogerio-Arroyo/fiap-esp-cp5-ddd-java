package br.com.fiap.bank.plataforma.infrastructure.gateway.corebancario;

import java.math.BigDecimal;

/**
 * DTO local de integracao, usado apenas para desserializar a resposta do
 * recurso REST /api/v1/contas/cliente/{cpf} do microsservico Core Bancario.
 * Deliberadamente desacoplado do DTO de resposta do Core Bancario (cada
 * microsservico possui seu proprio contrato de integracao).
 */
public record CoreContaDTO(
        String id,
        String numero,
        String agencia,
        String tipoConta,
        BigDecimal saldo,
        String status,
        String dataAbertura,
        String cpfCliente,
        String nomeCliente
) {
}
