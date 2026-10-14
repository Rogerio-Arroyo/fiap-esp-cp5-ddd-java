package br.com.fiap.bank.core.domain.model.vo;

import br.com.fiap.bank.core.domain.exception.SaldoInsuficienteException;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * Value Object que representa o saldo monetario de uma Conta Bancaria.
 * Imutavel: toda operacao de credito/debito retorna uma NOVA instancia de
 * Saldo, preservando as invariantes de dominio (saldo nunca fica negativo).
 */
public final class Saldo implements Serializable {

    public static final Saldo ZERO = new Saldo(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));

    private final BigDecimal valor;

    private Saldo(BigDecimal valor) {
        this.valor = valor;
    }

    public static Saldo of(BigDecimal valorInicial) {
        if (valorInicial == null) {
            throw new IllegalArgumentException("O valor do saldo nao pode ser nulo.");
        }
        if (valorInicial.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("O saldo inicial da conta nao pode ser negativo.");
        }
        return new Saldo(valorInicial.setScale(2, RoundingMode.HALF_UP));
    }

    public Saldo creditar(BigDecimal valor) {
        validarValorPositivo(valor);
        return new Saldo(this.valor.add(valor).setScale(2, RoundingMode.HALF_UP));
    }

    public Saldo debitar(BigDecimal valor) {
        validarValorPositivo(valor);
        BigDecimal resultante = this.valor.subtract(valor).setScale(2, RoundingMode.HALF_UP);
        if (resultante.compareTo(BigDecimal.ZERO) < 0) {
            throw new SaldoInsuficienteException(
                    "Saldo insuficiente para a operacao. Saldo atual: " + this.valor + ", valor solicitado: " + valor);
        }
        return new Saldo(resultante);
    }

    private void validarValorPositivo(BigDecimal valor) {
        if (valor == null || valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("O valor da operacao deve ser estritamente maior que zero.");
        }
    }

    public BigDecimal valor() {
        return valor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Saldo saldo)) return false;
        return valor.compareTo(saldo.valor) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(valor.stripTrailingZeros());
    }

    @Override
    public String toString() {
        return valor.toString();
    }
}
