package br.com.fiap.bank.plataforma.presentation.controller.legacy.dto;

/** Corpo de requisicao legado (formato preservado) para /api/credito/aprovarSolicitacao. */
public record AprovarSolicitacaoRequest(Long solicitacaoId, String parecerOperador) {
}
