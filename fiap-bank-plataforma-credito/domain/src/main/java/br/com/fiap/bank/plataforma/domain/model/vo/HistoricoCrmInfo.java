package br.com.fiap.bank.plataforma.domain.model.vo;

/**
 * Value Object que representa o historico de relacionamento do cliente no CRM
 * corporativo, consultado pela Plataforma de Credito durante a orquestracao
 * da analise de risco (ver Secao 1 do Checkpoint: "historico do cliente no CRM corporativo").
 */
public record HistoricoCrmInfo(
        boolean clienteFiel,
        int pontuacaoRelacionamento
) {
}
