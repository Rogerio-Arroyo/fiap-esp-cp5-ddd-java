package br.com.fiap.bank.plataforma.application.dto;

import br.com.fiap.bank.plataforma.domain.model.SolicitacaoCredito;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** Representacao de saida (Application layer) de uma Solicitacao de Credito. */
public record SolicitacaoCreditoResponse(
        Long id,
        String cpfCliente,
        String nomeCliente,
        String tipoCredito,
        BigDecimal valorSolicitado,
        Integer quantidadeParcelas,
        BigDecimal rendaMensal,
        Integer scoreCredito,
        String status,
        String parecerOperador,
        Boolean scrRestricoes,
        Integer scoreBureau,
        LocalDateTime dataSolicitacao,
        LocalDateTime dataAtualizacao
) {
    public static SolicitacaoCreditoResponse fromDomain(SolicitacaoCredito s) {
        return new SolicitacaoCreditoResponse(
                s.getId(),
                s.getCpfCliente().valor(),
                s.getNomeCliente(),
                s.getTipoCredito(),
                s.getValorSolicitado(),
                s.getQuantidadeParcelas(),
                s.getRendaMensal(),
                s.getScoreCredito(),
                s.getStatus() != null ? s.getStatus().name() : null,
                s.getParecer(),
                s.getScrRestricoes(),
                s.getScoreBureau(),
                s.getDataSolicitacao(),
                s.getDataAtualizacao()
        );
    }
}
