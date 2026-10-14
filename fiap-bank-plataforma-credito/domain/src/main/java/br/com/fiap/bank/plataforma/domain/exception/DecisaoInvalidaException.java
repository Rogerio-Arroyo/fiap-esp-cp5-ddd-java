package br.com.fiap.bank.plataforma.domain.exception;

/** Lancada quando a Mesa de Operacoes tenta registrar uma decisao de credito em um status nao suportado. */
public class DecisaoInvalidaException extends RuntimeException {
    public DecisaoInvalidaException(String message) {
        super(message);
    }
}
