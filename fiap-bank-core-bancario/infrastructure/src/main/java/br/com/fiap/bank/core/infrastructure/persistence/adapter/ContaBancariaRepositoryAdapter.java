package br.com.fiap.bank.core.infrastructure.persistence.adapter;

import br.com.fiap.bank.core.domain.model.ContaBancaria;
import br.com.fiap.bank.core.domain.repository.ContaBancariaRepository;
import br.com.fiap.bank.core.infrastructure.persistence.entity.ClienteJpaEntity;
import br.com.fiap.bank.core.infrastructure.persistence.mapper.ContaBancariaEntityMapper;
import br.com.fiap.bank.core.infrastructure.persistence.repository.ClienteJpaRepository;
import br.com.fiap.bank.core.infrastructure.persistence.repository.ContaBancariaJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Adaptador de persistencia (Repository Adapter) que implementa a porta de
 * dominio ContaBancariaRepository usando Spring Data JPA, traduzindo entre a
 * Entidade de dominio pura e a Entidade JPA.
 */
@Component
public class ContaBancariaRepositoryAdapter implements ContaBancariaRepository {

    private final ContaBancariaJpaRepository jpaRepository;
    private final ClienteJpaRepository clienteJpaRepository;

    public ContaBancariaRepositoryAdapter(ContaBancariaJpaRepository jpaRepository,
                                           ClienteJpaRepository clienteJpaRepository) {
        this.jpaRepository = jpaRepository;
        this.clienteJpaRepository = clienteJpaRepository;
    }

    @Override
    public ContaBancaria save(ContaBancaria conta) {
        ClienteJpaEntity clienteRef = clienteJpaRepository.getReferenceById(conta.getClienteId());
        var salva = jpaRepository.save(ContaBancariaEntityMapper.toJpaEntity(conta, clienteRef));
        return ContaBancariaEntityMapper.toDomain(salva);
    }

    @Override
    public Optional<ContaBancaria> findById(UUID id) {
        return jpaRepository.findById(id).map(ContaBancariaEntityMapper::toDomain);
    }

    @Override
    public Optional<ContaBancaria> findByNumero(String numero) {
        return jpaRepository.findByNumero(numero).map(ContaBancariaEntityMapper::toDomain);
    }

    @Override
    public List<ContaBancaria> findByClienteId(UUID clienteId) {
        return jpaRepository.findByCliente_Id(clienteId).stream().map(ContaBancariaEntityMapper::toDomain).toList();
    }

    @Override
    public List<ContaBancaria> findByClienteCpf(String cpf) {
        return jpaRepository.findByCliente_Cpf(cpf).stream().map(ContaBancariaEntityMapper::toDomain).toList();
    }

    @Override
    public List<ContaBancaria> findAll() {
        return jpaRepository.findAll().stream().map(ContaBancariaEntityMapper::toDomain).toList();
    }

    @Override
    public boolean existsByNumero(String numero) {
        return jpaRepository.existsByNumero(numero);
    }
}
