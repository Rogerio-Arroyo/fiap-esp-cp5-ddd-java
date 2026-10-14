package br.com.fiap.bank.plataforma.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

/** Contrato de entrada (Application layer) para a submissao de uma nova proposta de credito. */
public record SolicitarCreditoRequest(

        @NotBlank(message = "O CPF do cliente e obrigatorio.")
        @Pattern(regexp = "\\d{11}|\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}",
                message = "O CPF deve conter exatamente 11 digitos numericos ou o formato canonico XXX.XXX.XXX-XX.")
        String cpfCliente,

        @NotBlank(message = "O nome do cliente e obrigatorio.")
        @Size(min = 3, max = 150, message = "O nome deve ter entre 3 e 150 caracteres.")
        String nomeCliente,

        @NotBlank(message = "O tipo de credito e obrigatorio.")
        String tipoCredito,

        @NotNull(message = "O valor solicitado e obrigatorio.")
        @Positive(message = "O valor solicitado deve ser estritamente maior que zero.")
        BigDecimal valorSolicitado,

        @NotNull(message = "A quantidade de parcelas e obrigatoria.")
        @Positive(message = "A quantidade de parcelas deve ser estritamente maior que zero.")
        Integer quantidadeParcelas,

        @NotNull(message = "A renda mensal e obrigatoria.")
        @PositiveOrZero(message = "A renda mensal nao pode ser negativa.")
        BigDecimal rendaMensal
) {
}
