package br.com.fiap.bank.core.domain.exception;

/** Lancada quando um Cliente (correntista) nao e localizado pelo identificador ou CPF informado. */
public class ClienteNaoEncontradoException extends RuntimeException {
    public ClienteNaoEncontradoException(String message) {
        super(message);
    }
}
