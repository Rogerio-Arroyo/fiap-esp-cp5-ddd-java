package br.com.fiap.bank.plataforma.presentation.controller.legacy.dto;

/** Corpo de requisicao legado (formato preservado) para /api/credito/reprovarSolicitacao. */
public record ReprovarSolicitacaoRequest(Long solicitacaoId, String motivoReprovacao) {
}
