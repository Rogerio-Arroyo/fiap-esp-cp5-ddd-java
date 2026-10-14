package br.com.fiap.bank.core.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

/** Contrato de entrada para a movimentacao atomica (deposito/saque) do sub-recurso saldo de uma Conta Bancaria. */
public record AtualizarSaldoRequest(

        @NotBlank(message = "A operacao e obrigatoria.")
        @Pattern(regexp = "(?i)DEPOSITO|SAQUE", message = "A operacao deve ser DEPOSITO ou SAQUE.")
        String operacao,

        @NotNull(message = "O valor da operacao e obrigatorio.")
        @Positive(message = "O valor da operacao deve ser estritamente maior que zero.")
        BigDecimal valor
) {
    public boolean isDeposito() {
        return "DEPOSITO".equalsIgnoreCase(operacao);
    }
}
