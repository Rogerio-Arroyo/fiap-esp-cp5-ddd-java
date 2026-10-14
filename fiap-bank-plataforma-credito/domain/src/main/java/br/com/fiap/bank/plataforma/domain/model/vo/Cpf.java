package br.com.fiap.bank.plataforma.domain.model.vo;

import br.com.fiap.bank.plataforma.domain.exception.CpfInvalidoException;

import java.io.Serializable;
import java.util.Objects;

/**
 * Value Object que representa o CPF do cliente solicitante de credito.
 * <p>
 * A Plataforma de Credito e um canal de ENTRADA de propostas vindas de multiplos
 * canais (Internet Banking, parceiros, etc.) e nao e a fonte de verdade cadastral
 * do correntista (essa responsabilidade e do Core Bancario). Por isso, este VO
 * aplica uma validacao estrutural (11 digitos numericos), mais branda que a
 * validacao de digito verificador do Core Bancario, preservando a
 * retrocompatibilidade com os front-ends legados.
 */
public final class Cpf implements Serializable {

    private final String valor;

    private Cpf(String valor) {
        this.valor = valor;
    }

    public static Cpf of(String entrada) {
        if (entrada == null || entrada.isBlank()) {
            throw new CpfInvalidoException("O CPF do cliente e obrigatorio.");
        }
        String digitos = entrada.replaceAll("\\D", "");
        if (digitos.length() != 11) {
            throw new CpfInvalidoException("CPF invalido: deve conter exatamente 11 digitos numericos (" + entrada + ").");
        }
        return new Cpf(digitos);
    }

    public String valor() {
        return valor;
    }

    public String formatado() {
        return valor.substring(0, 3) + "." + valor.substring(3, 6) + "." + valor.substring(6, 9) + "-" + valor.substring(9, 11);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Cpf cpf)) return false;
        return valor.equals(cpf.valor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(valor);
    }

    @Override
    public String toString() {
        return formatado();
    }
}
