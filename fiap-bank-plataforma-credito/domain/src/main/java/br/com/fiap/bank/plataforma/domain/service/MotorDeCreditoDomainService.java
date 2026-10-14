package br.com.fiap.bank.plataforma.domain.service;

import br.com.fiap.bank.plataforma.domain.model.SolicitacaoCredito;
import br.com.fiap.bank.plataforma.domain.model.vo.StatusSolicitacao;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Servico de dominio PURO (sem dependencia de framework) que concentra o motor
 * de regras de concessao de credito do FIAP Bank. Recebe os insumos ja
 * coletados pela camada de aplicacao junto aos sistemas externos (SCR, Bureau
 * de Credito) e devolve a decisao (status + parecer), sem conhecer HTTP,
 * persistencia ou qualquer outro detalhe de infraestrutura.
 */
public final class MotorDeCreditoDomainService {

    private static final BigDecimal PERCENTUAL_MAXIMO_COMPROMETIMENTO_RENDA = new BigDecimal("0.30");
    private static final int SCORE_MINIMO_BUREAU = 500;

    private MotorDeCreditoDomainService() {
    }

    /** Avalia a proposta de credito e devolve a Decisao (status + parecer) do Motor de Credito. */
    public static Decisao avaliar(SolicitacaoCredito solicitacao, boolean temRestricaoScr, int scoreBureau) {
        if (temRestricaoScr) {
            return new Decisao(StatusSolicitacao.REPROVADO,
                    "Solicitacao reprovada automaticamente devido a restricoes ativas no SCR (Banco Central).");
        }

        if (scoreBureau < SCORE_MINIMO_BUREAU) {
            return new Decisao(StatusSolicitacao.REPROVADO,
                    "Solicitacao reprovada automaticamente. Score no Bureau de Credito insuficiente (" + scoreBureau + ").");
        }

        BigDecimal parcelaEstimada = solicitacao.getValorSolicitado()
                .divide(BigDecimal.valueOf(solicitacao.getQuantidadeParcelas()), 2, RoundingMode.HALF_UP);
        BigDecimal limiteComprometimentoRenda = solicitacao.getRendaMensal().multiply(PERCENTUAL_MAXIMO_COMPROMETIMENTO_RENDA);

        if (parcelaEstimada.compareTo(limiteComprometimentoRenda) > 0) {
            return new Decisao(StatusSolicitacao.EM_ANALISE,
                    "Encaminhado para a Mesa de Operacoes de Credito. Parcela estimada (R$ " + parcelaEstimada
                            + ") compromete mais de 30% da renda informada.");
        }

        return new Decisao(StatusSolicitacao.APROVADO, "Solicitacao aprovada automaticamente pelo Motor de Credito FIAP Bank!");
    }

    /** Resultado imutavel da avaliacao automatica do Motor de Credito. */
    public record Decisao(StatusSolicitacao status, String parecer) {
    }
}
