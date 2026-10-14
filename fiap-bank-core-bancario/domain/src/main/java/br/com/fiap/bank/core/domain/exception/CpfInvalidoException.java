package br.com.fiap.bank.core.domain.exception;

/** Lancada quando um CPF informado nao atende as regras de formacao/validacao do dominio. */
public class CpfInvalidoException extends RuntimeException {
    public CpfInvalidoException(String message) {
        super(message);
    }
}
