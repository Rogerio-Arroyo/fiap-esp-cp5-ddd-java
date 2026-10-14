package br.com.fiap.bank.core.infrastructure.persistence.repository;

import br.com.fiap.bank.core.infrastructure.persistence.entity.ContaBancariaJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/** Repositorio Spring Data JPA responsavel pelo acesso relacional a tabela tb_conta_bancaria. */
public interface ContaBancariaJpaRepository extends JpaRepository<ContaBancariaJpaEntity, UUID> {

    Optional<ContaBancariaJpaEntity> findByNumero(String numero);

    List<ContaBancariaJpaEntity> findByCliente_Id(UUID clienteId);

    List<ContaBancariaJpaEntity> findByCliente_Cpf(String cpf);

    boolean existsByNumero(String numero);
}
