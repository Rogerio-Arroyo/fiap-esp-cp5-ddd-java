package br.com.fiap.bank.core.infrastructure.persistence.mapper;

import br.com.fiap.bank.core.domain.model.ContaBancaria;
import br.com.fiap.bank.core.domain.model.vo.StatusConta;
import br.com.fiap.bank.core.domain.model.vo.TipoConta;
import br.com.fiap.bank.core.infrastructure.persistence.entity.ClienteJpaEntity;
import br.com.fiap.bank.core.infrastructure.persistence.entity.ContaBancariaJpaEntity;

/** Conversor entre a Entidade de dominio pura ContaBancaria e a sua Entidade JPA correspondente. */
public final class ContaBancariaEntityMapper {

    private ContaBancariaEntityMapper() {
    }

    public static ContaBancaria toDomain(ContaBancariaJpaEntity jpaEntity) {
        if (jpaEntity == null) {
            return null;
        }
        return ContaBancaria.restaurar(
                jpaEntity.getId(),
                jpaEntity.getNumero(),
                jpaEntity.getAgencia(),
                TipoConta.valueOf(jpaEntity.getTipoConta()),
                jpaEntity.getCliente().getId(),
                jpaEntity.getDataAbertura(),
                jpaEntity.getSaldo(),
                StatusConta.valueOf(jpaEntity.getStatus())
        );
    }

    /** Converte para Entidade JPA, recebendo a referencia gerenciada do ClienteJpaEntity a ser vinculada. */
    public static ContaBancariaJpaEntity toJpaEntity(ContaBancaria conta, ClienteJpaEntity clienteRef) {
        if (conta == null) {
            return null;
        }
        return ContaBancariaJpaEntity.builder()
                .id(conta.getId())
                .numero(conta.getNumero())
                .agencia(conta.getAgencia())
                .tipoConta(conta.getTipoConta().name())
                .status(conta.getStatus().name())
                .saldo(conta.getSaldo().valor())
                .dataAbertura(conta.getDataAbertura())
                .cliente(clienteRef)
                .build();
    }
}
