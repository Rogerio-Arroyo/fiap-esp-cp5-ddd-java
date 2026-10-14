package br.com.fiap.bank.plataforma.domain.exception;

/** Lancada quando uma Solicitacao de Credito nao e localizada pelo identificador informado. */
public class SolicitacaoCreditoNaoEncontradaException extends RuntimeException {
    public SolicitacaoCreditoNaoEncontradaException(String message) {
        super(message);
    }
}
