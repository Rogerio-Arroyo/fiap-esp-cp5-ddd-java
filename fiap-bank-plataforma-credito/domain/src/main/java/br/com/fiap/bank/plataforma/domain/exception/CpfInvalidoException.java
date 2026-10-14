package br.com.fiap.bank.plataforma.domain.exception;

/** Lancada quando um CPF informado nao possui estrutura numerica valida (11 digitos). */
public class CpfInvalidoException extends RuntimeException {
    public CpfInvalidoException(String message) {
        super(message);
    }
}
