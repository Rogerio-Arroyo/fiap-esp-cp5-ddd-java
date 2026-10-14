package br.com.fiap.bank.core.domain.exception;

/** Lancada quando uma Conta Bancaria nao e localizada pelo identificador ou numero informado. */
public class ContaBancariaNaoEncontradaException extends RuntimeException {
    public ContaBancariaNaoEncontradaException(String message) {
        super(message);
    }
}
