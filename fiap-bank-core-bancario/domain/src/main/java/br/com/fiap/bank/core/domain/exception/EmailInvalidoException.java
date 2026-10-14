package br.com.fiap.bank.core.domain.exception;

/** Lancada quando um e-mail informado nao atende ao formato valido esperado pelo dominio. */
public class EmailInvalidoException extends RuntimeException {
    public EmailInvalidoException(String message) {
        super(message);
    }
}
