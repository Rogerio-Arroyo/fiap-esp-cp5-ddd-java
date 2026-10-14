package br.com.fiap.bank.core.domain.exception;

/** Lancada ao tentar cadastrar um Cliente com um CPF que ja possui registro no Core Bancario. */
public class CpfJaCadastradoException extends RuntimeException {
    public CpfJaCadastradoException(String message) {
        super(message);
    }
}
