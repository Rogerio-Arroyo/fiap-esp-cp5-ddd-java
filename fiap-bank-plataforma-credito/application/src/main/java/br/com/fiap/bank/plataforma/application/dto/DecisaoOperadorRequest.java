package br.com.fiap.bank.plataforma.application.dto;

import br.com.fiap.bank.plataforma.domain.model.vo.StatusSolicitacao;
import jakarta.validation.constraints.NotNull;

/**
 * Contrato de entrada para o registro da decisao do operador da Mesa de
 * Operacoes de Credito sobre uma proposta em analise (sub-recurso /status).
 */
public record DecisaoOperadorRequest(

        @NotNull(message = "A decisao (status) e obrigatoria.")
        StatusSolicitacao status,

        String observacao
) {
}
