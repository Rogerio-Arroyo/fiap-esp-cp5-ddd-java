package br.com.fiap.bank.core.application.dto;

import br.com.fiap.bank.core.domain.model.Cliente;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/** Representacao de saida (Application layer) de um Cliente correntista. */
public record ClienteResponse(
        UUID id,
        String cpf,
        String nome,
        String email,
        LocalDate dataNascimento,
        BigDecimal rendaMensal,
        LocalDate dataCadastro
) {
    public static ClienteResponse fromDomain(Cliente cliente) {
        return new ClienteResponse(
                cliente.getId(),
                cliente.getCpf().valor(),
                cliente.getNome(),
                cliente.getEmail().valor(),
                cliente.getDataNascimento(),
                cliente.getRendaMensal(),
                cliente.getDataCadastro()
        );
    }
}
