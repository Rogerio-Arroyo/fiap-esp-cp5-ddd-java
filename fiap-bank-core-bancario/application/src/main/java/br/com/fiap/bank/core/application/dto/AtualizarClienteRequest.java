package br.com.fiap.bank.core.application.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

/** Contrato de entrada para a substituicao integral (PUT) dos dados cadastrais de um Cliente. CPF e data de nascimento sao imutaveis. */
public record AtualizarClienteRequest(

        @NotBlank(message = "O nome e obrigatorio.")
        @Size(min = 3, max = 150, message = "O nome deve ter entre 3 e 150 caracteres.")
        String nome,

        @NotBlank(message = "O e-mail e obrigatorio.")
        @Email(message = "O e-mail informado nao e valido.")
        String email,

        @NotNull(message = "A renda mensal e obrigatoria.")
        @PositiveOrZero(message = "A renda mensal nao pode ser negativa.")
        BigDecimal rendaMensal
) {
}
