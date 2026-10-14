package br.com.fiap.bank.core.domain.exception;

/** Lancada quando se tenta movimentar ou operar uma Conta Bancaria que nao esta ATIVA. */
public class ContaInativaException extends RuntimeException {
    public ContaInativaException(String message) {
        super(message);
    }
}
