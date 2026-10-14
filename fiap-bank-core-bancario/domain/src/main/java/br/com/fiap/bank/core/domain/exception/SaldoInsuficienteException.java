package br.com.fiap.bank.core.domain.exception;

/** Lancada quando uma operacao de debito viola a invariante de saldo nao-negativo da Conta Bancaria. */
public class SaldoInsuficienteException extends RuntimeException {
    public SaldoInsuficienteException(String message) {
        super(message);
    }
}
