package br.com.fiap.bank.core.domain.model.vo;

import br.com.fiap.bank.core.domain.exception.CpfInvalidoException;

import java.io.Serializable;
import java.util.Objects;

/**
 * Value Object que representa um CPF valido (Cadastro de Pessoa Fisica).
 * Imutavel, auto-validado e comparado por valor (equals/hashCode), conforme
 * as boas praticas de Objetos de Valor do Domain-Driven Design.
 */
public final class Cpf implements Serializable {

    private final String valor;

    private Cpf(String valor) {
        this.valor = valor;
    }

    public static Cpf of(String entrada) {
        if (entrada == null || entrada.isBlank()) {
            throw new CpfInvalidoException("O CPF e obrigatorio.");
        }
        String digitos = entrada.replaceAll("\\D", "");
        if (!isValido(digitos)) {
            throw new CpfInvalidoException("CPF invalido: " + entrada);
        }
        return new Cpf(digitos);
    }

    private static boolean isValido(String cpf) {
        if (cpf.length() != 11 || cpf.chars().distinct().count() == 1) {
            return false;
        }
        try {
            int d1 = calcularDigitoVerificador(cpf.substring(0, 9), 10);
            int d2 = calcularDigitoVerificador(cpf.substring(0, 9) + d1, 11);
            return cpf.equals(cpf.substring(0, 9) + d1 + d2);
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private static int calcularDigitoVerificador(String base, int pesoInicial) {
        int soma = 0;
        int peso = pesoInicial;
        for (char c : base.toCharArray()) {
            soma += Character.getNumericValue(c) * peso--;
        }
        int resto = soma % 11;
        return resto < 2 ? 0 : 11 - resto;
    }

    /** Retorna o CPF apenas com digitos numericos (sem mascara), forma canonica de persistencia. */
    public String valor() {
        return valor;
    }

    /** Retorna o CPF formatado com mascara XXX.XXX.XXX-XX, para exibicao. */
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
