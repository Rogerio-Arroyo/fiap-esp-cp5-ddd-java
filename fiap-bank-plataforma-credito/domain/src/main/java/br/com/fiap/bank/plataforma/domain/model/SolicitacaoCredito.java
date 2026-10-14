package br.com.fiap.bank.plataforma.domain.model;

import br.com.fiap.bank.plataforma.domain.exception.DecisaoInvalidaException;
import br.com.fiap.bank.plataforma.domain.model.vo.Cpf;
import br.com.fiap.bank.plataforma.domain.model.vo.StatusSolicitacao;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Entidade de dominio pura que representa uma Solicitacao de Credito no FIAP Bank.
 * Nao possui nenhuma anotacao de persistencia ou dependencia de framework.
 */
@Getter
public class SolicitacaoCredito {

    private final Long id;
    private final Cpf cpfCliente;
    private final String nomeCliente;
    private final String tipoCredito;
    private final BigDecimal valorSolicitado;
    private final Integer quantidadeParcelas;
    private final BigDecimal rendaMensal;
    private Integer scoreCredito;
    private StatusSolicitacao status;
    private String parecer;
    private Boolean scrRestricoes;
    private Integer scoreBureau;
    private final LocalDateTime dataSolicitacao;
    private LocalDateTime dataAtualizacao;

    private SolicitacaoCredito(Long id, Cpf cpfCliente, String nomeCliente, String tipoCredito,
                                BigDecimal valorSolicitado, Integer quantidadeParcelas, BigDecimal rendaMensal,
                                Integer scoreCredito, StatusSolicitacao status, String parecer, Boolean scrRestricoes,
                                Integer scoreBureau, LocalDateTime dataSolicitacao, LocalDateTime dataAtualizacao) {
        this.id = id;
        this.cpfCliente = cpfCliente;
        this.nomeCliente = nomeCliente;
        this.tipoCredito = tipoCredito;
        this.valorSolicitado = valorSolicitado;
        this.quantidadeParcelas = quantidadeParcelas;
        this.rendaMensal = rendaMensal;
        this.scoreCredito = scoreCredito;
        this.status = status;
        this.parecer = parecer;
        this.scrRestricoes = scrRestricoes;
        this.scoreBureau = scoreBureau;
        this.dataSolicitacao = dataSolicitacao;
        this.dataAtualizacao = dataAtualizacao;
    }

    /** Factory de criacao de uma nova proposta de credito, ainda pendente de avaliacao pelo Motor de Credito. */
    public static SolicitacaoCredito nova(Cpf cpfCliente, String nomeCliente, String tipoCredito,
                                           BigDecimal valorSolicitado, Integer quantidadeParcelas, BigDecimal rendaMensal) {
        if (valorSolicitado == null || valorSolicitado.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("O valor solicitado deve ser estritamente maior que zero.");
        }
        if (quantidadeParcelas == null || quantidadeParcelas <= 0) {
            throw new IllegalArgumentException("A quantidade de parcelas deve ser estritamente maior que zero.");
        }
        LocalDateTime agora = LocalDateTime.now();
        return new SolicitacaoCredito(null, cpfCliente, nomeCliente, tipoCredito, valorSolicitado,
                quantidadeParcelas, rendaMensal, null, null, null, null, null, agora, agora);
    }

    /** Factory de reconstrucao a partir de dados ja persistidos (usada pelos adaptadores de infraestrutura). */
    public static SolicitacaoCredito restaurar(Long id, Cpf cpfCliente, String nomeCliente, String tipoCredito,
                                                BigDecimal valorSolicitado, Integer quantidadeParcelas, BigDecimal rendaMensal,
                                                Integer scoreCredito, StatusSolicitacao status, String parecer,
                                                Boolean scrRestricoes, Integer scoreBureau,
                                                LocalDateTime dataSolicitacao, LocalDateTime dataAtualizacao) {
        return new SolicitacaoCredito(id, cpfCliente, nomeCliente, tipoCredito, valorSolicitado, quantidadeParcelas,
                rendaMensal, scoreCredito, status, parecer, scrRestricoes, scoreBureau, dataSolicitacao, dataAtualizacao);
    }

    /** Registra o resultado da avaliacao automatica do Motor de Credito (ver MotorDeCreditoDomainService). */
    public void registrarAvaliacaoAutomatica(boolean scrRestricoes, int scoreBureau, StatusSolicitacao status, String parecer) {
        this.scrRestricoes = scrRestricoes;
        this.scoreBureau = scoreBureau;
        this.scoreCredito = scoreBureau;
        this.status = Objects.requireNonNull(status, "O status resultante da avaliacao e obrigatorio.");
        this.parecer = parecer;
        this.dataAtualizacao = LocalDateTime.now();
    }

    /** Registra a decisao manual de um operador da Mesa de Operacoes de Credito (aprovacao ou reprovacao). */
    public void registrarDecisaoOperador(StatusSolicitacao decisao, String observacao) {
        if (decisao != StatusSolicitacao.APROVADO && decisao != StatusSolicitacao.REPROVADO) {
            throw new DecisaoInvalidaException("A decisao do operador deve ser APROVADO ou REPROVADO (recebido: " + decisao + ").");
        }
        this.status = decisao;
        String prefixo = decisao == StatusSolicitacao.APROVADO ? "Aprovado pela Mesa de Operacoes: " : "Reprovado pela Mesa de Operacoes. Motivo: ";
        this.parecer = prefixo + (observacao != null && !observacao.isBlank() ? observacao : "OK");
        this.dataAtualizacao = LocalDateTime.now();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SolicitacaoCredito that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
