package br.com.fiap.bank.plataforma.domain.model.vo;

/** Enumeracao de dominio que representa o ciclo de vida de uma Solicitacao de Credito. */
public enum StatusSolicitacao {
    EM_ANALISE,
    APROVADO,
    REPROVADO,
    DOCUMENTACAO_PENDENTE
}
