package br.com.fiap.bank.core.infrastructure.persistence.mapper;

import br.com.fiap.bank.core.domain.model.Cliente;
import br.com.fiap.bank.core.domain.model.vo.Cpf;
import br.com.fiap.bank.core.domain.model.vo.Email;
import br.com.fiap.bank.core.infrastructure.persistence.entity.ClienteJpaEntity;

/** Conversor entre a Entidade de dominio pura Cliente e a sua Entidade JPA correspondente. */
public final class ClienteEntityMapper {

    private ClienteEntityMapper() {
    }

    public static Cliente toDomain(ClienteJpaEntity jpaEntity) {
        if (jpaEntity == null) {
            return null;
        }
        return Cliente.restaurar(
                jpaEntity.getId(),
                Cpf.of(jpaEntity.getCpf()),
                jpaEntity.getNome(),
                Email.of(jpaEntity.getEmail()),
                jpaEntity.getDataNascimento(),
                jpaEntity.getRendaMensal(),
                jpaEntity.getDataCadastro()
        );
    }

    public static ClienteJpaEntity toJpaEntity(Cliente cliente) {
        if (cliente == null) {
            return null;
        }
        return ClienteJpaEntity.builder()
                .id(cliente.getId())
                .cpf(cliente.getCpf().valor())
                .nome(cliente.getNome())
                .email(cliente.getEmail().valor())
                .dataNascimento(cliente.getDataNascimento())
                .rendaMensal(cliente.getRendaMensal())
                .dataCadastro(cliente.getDataCadastro())
                .build();
    }
}
