package br.com.fiap.bank.core.application.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

/** Contrato de entrada (Application layer) para a criacao de um novo Cliente correntista. */
public record CriarClienteRequest(

        @NotBlank(message = "O CPF e obrigatorio.")
        @Pattern(regexp = "\\d{11}|\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}",
                message = "O CPF deve conter exatamente 11 digitos numericos ou o formato canonico XXX.XXX.XXX-XX.")
        String cpf,

        @NotBlank(message = "O nome e obrigatorio.")
        @Size(min = 3, max = 150, message = "O nome deve ter entre 3 e 150 caracteres.")
        String nome,

        @NotBlank(message = "O e-mail e obrigatorio.")
        @Email(message = "O e-mail informado nao e valido.")
        String email,

        @NotNull(message = "A data de nascimento e obrigatoria.")
        @Past(message = "A data de nascimento deve ser uma data no passado.")
        LocalDate dataNascimento,

        @NotNull(message = "A renda mensal e obrigatoria.")
        @PositiveOrZero(message = "A renda mensal nao pode ser negativa.")
        BigDecimal rendaMensal
) {
}
