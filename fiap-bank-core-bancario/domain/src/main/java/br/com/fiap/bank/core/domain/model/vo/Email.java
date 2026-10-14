package br.com.fiap.bank.core.domain.model.vo;

import br.com.fiap.bank.core.domain.exception.EmailInvalidoException;

import java.io.Serializable;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Value Object que representa um endereco de e-mail valido. Imutavel e
 * comparado por valor.
 */
public final class Email implements Serializable {

    private static final Pattern RFC_SIMPLIFICADO =
            Pattern.compile("^[\\w.+-]+@[\\w-]+\\.[a-zA-Z]{2,}(\\.[a-zA-Z]{2,})?$");

    private final String valor;

    private Email(String valor) {
        this.valor = valor;
    }

    public static Email of(String entrada) {
        if (entrada == null || entrada.isBlank()) {
            throw new EmailInvalidoException("O e-mail e obrigatorio.");
        }
        String normalizado = entrada.trim();
        if (!RFC_SIMPLIFICADO.matcher(normalizado).matches()) {
            throw new EmailInvalidoException("E-mail invalido: " + entrada);
        }
        return new Email(normalizado);
    }

    public String valor() {
        return valor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Email email)) return false;
        return valor.equalsIgnoreCase(email.valor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(valor.toLowerCase());
    }

    @Override
    public String toString() {
        return valor;
    }
}
