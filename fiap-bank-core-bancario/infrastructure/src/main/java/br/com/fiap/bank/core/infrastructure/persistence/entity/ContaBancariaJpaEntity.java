package br.com.fiap.bank.core.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Entidade JPA (mapeamento relacional) da Conta Bancaria. Pertence exclusivamente
 * a camada infrastructure, mantendo o dominio puro totalmente desacoplado do Hibernate/JPA.
 */
@Entity
@Table(name = "tb_conta_bancaria")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContaBancariaJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "numero", nullable = false, unique = true, length = 20)
    private String numero;

    @Column(name = "agencia", nullable = false, length = 4)
    private String agencia;

    @Column(name = "tipo_conta", nullable = false, length = 20)
    private String tipoConta;

    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @Column(name = "saldo", nullable = false, precision = 15, scale = 2)
    private BigDecimal saldo;

    @Column(name = "data_abertura", nullable = false)
    private LocalDate dataAbertura;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private ClienteJpaEntity cliente;
}
