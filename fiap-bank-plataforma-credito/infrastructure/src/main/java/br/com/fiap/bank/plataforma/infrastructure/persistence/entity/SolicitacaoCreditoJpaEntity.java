package br.com.fiap.bank.plataforma.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entidade JPA (mapeamento relacional) da Solicitacao de Credito. Pertence
 * exclusivamente a camada infrastructure: o modelo de dominio puro
 * (SolicitacaoCredito) nunca e anotado com @Entity/@Table/@Column.
 */
@Entity
@Table(name = "tb_solicitacao_credito")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SolicitacaoCreditoJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "cpf_cliente", nullable = false, length = 11)
    private String cpfCliente;

    @Column(name = "nome_cliente", nullable = false)
    private String nomeCliente;

    @Column(name = "tipo_credito", nullable = false)
    private String tipoCredito;

    @Column(name = "valor_solicitado", nullable = false, precision = 15, scale = 2)
    private BigDecimal valorSolicitado;

    @Column(name = "quantidade_parcelas", nullable = false)
    private Integer quantidadeParcelas;

    @Column(name = "renda_mensal", nullable = false, precision = 15, scale = 2)
    private BigDecimal rendaMensal;

    @Column(name = "score_credito")
    private Integer scoreCredito;

    @Column(name = "status", nullable = false, length = 25)
    private String status;

    @Column(name = "parecer", length = 500)
    private String parecer;

    @Column(name = "scr_restricoes")
    private Boolean scrRestricoes;

    @Column(name = "score_bureau")
    private Integer scoreBureau;

    @Column(name = "data_solicitacao", nullable = false)
    private LocalDateTime dataSolicitacao;

    @Column(name = "data_atualizacao")
    private LocalDateTime dataAtualizacao;
}
