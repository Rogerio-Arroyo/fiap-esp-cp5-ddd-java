package br.com.fiap.bank.plataforma.infrastructure.persistence.mapper;

import br.com.fiap.bank.plataforma.domain.model.SolicitacaoCredito;
import br.com.fiap.bank.plataforma.domain.model.vo.Cpf;
import br.com.fiap.bank.plataforma.domain.model.vo.StatusSolicitacao;
import br.com.fiap.bank.plataforma.infrastructure.persistence.entity.SolicitacaoCreditoJpaEntity;

/** Conversor entre a Entidade de dominio pura SolicitacaoCredito e a sua Entidade JPA correspondente. */
public final class SolicitacaoCreditoEntityMapper {

    private SolicitacaoCreditoEntityMapper() {
    }

    public static SolicitacaoCredito toDomain(SolicitacaoCreditoJpaEntity jpaEntity) {
        if (jpaEntity == null) {
            return null;
        }
        return SolicitacaoCredito.restaurar(
                jpaEntity.getId(),
                Cpf.of(jpaEntity.getCpfCliente()),
                jpaEntity.getNomeCliente(),
                jpaEntity.getTipoCredito(),
                jpaEntity.getValorSolicitado(),
                jpaEntity.getQuantidadeParcelas(),
                jpaEntity.getRendaMensal(),
                jpaEntity.getScoreCredito(),
                jpaEntity.getStatus() != null ? StatusSolicitacao.valueOf(jpaEntity.getStatus()) : null,
                jpaEntity.getParecer(),
                jpaEntity.getScrRestricoes(),
                jpaEntity.getScoreBureau(),
                jpaEntity.getDataSolicitacao(),
                jpaEntity.getDataAtualizacao()
        );
    }

    public static SolicitacaoCreditoJpaEntity toJpaEntity(SolicitacaoCredito solicitacao) {
        if (solicitacao == null) {
            return null;
        }
        return SolicitacaoCreditoJpaEntity.builder()
                .id(solicitacao.getId())
                .cpfCliente(solicitacao.getCpfCliente().valor())
                .nomeCliente(solicitacao.getNomeCliente())
                .tipoCredito(solicitacao.getTipoCredito())
                .valorSolicitado(solicitacao.getValorSolicitado())
                .quantidadeParcelas(solicitacao.getQuantidadeParcelas())
                .rendaMensal(solicitacao.getRendaMensal())
                .scoreCredito(solicitacao.getScoreCredito())
                .status(solicitacao.getStatus() != null ? solicitacao.getStatus().name() : null)
                .parecer(solicitacao.getParecer())
                .scrRestricoes(solicitacao.getScrRestricoes())
                .scoreBureau(solicitacao.getScoreBureau())
                .dataSolicitacao(solicitacao.getDataSolicitacao())
                .dataAtualizacao(solicitacao.getDataAtualizacao())
                .build();
    }
}
