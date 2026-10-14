package br.com.fiap.bank.core.application.dto;

import br.com.fiap.bank.core.domain.model.vo.TipoConta;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

/** Contrato de entrada para a abertura de uma nova Conta Bancaria vinculada a um Cliente ja existente. */
public record CriarContaRequest(

        @NotBlank(message = "O CPF do cliente titular e obrigatorio.")
        String cpfCliente,

        @Pattern(regexp = "\\d{4}", message = "A agencia deve possuir exatamente 4 digitos numericos.")
        String agencia,

        @NotNull(message = "O tipo de conta e obrigatorio.")
        TipoConta tipoConta,

        @NotNull(message = "O saldo inicial e obrigatorio.")
        @PositiveOrZero(message = "O saldo inicial da conta nao pode ser negativo.")
        BigDecimal saldoInicial
) {
    private static final String AGENCIA_PADRAO = "0001";

    /** Resolve a agencia informada, assumindo "0001" como valor padrao quando ausente/nulo. */
    public String agenciaOuPadrao() {
        return (agencia == null || agencia.isBlank()) ? AGENCIA_PADRAO : agencia;
    }
}
