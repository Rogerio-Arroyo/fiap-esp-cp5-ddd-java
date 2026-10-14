package br.com.fiap.bank.core.infrastructure.persistence.adapter;

import br.com.fiap.bank.core.domain.model.Cliente;
import br.com.fiap.bank.core.domain.model.vo.Cpf;
import br.com.fiap.bank.core.domain.repository.ClienteRepository;
import br.com.fiap.bank.core.infrastructure.persistence.mapper.ClienteEntityMapper;
import br.com.fiap.bank.core.infrastructure.persistence.repository.ClienteJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Adaptador de persistencia (Repository Adapter) que implementa a porta de
 * dominio ClienteRepository usando Spring Data JPA, traduzindo entre a
 * Entidade de dominio pura e a Entidade JPA.
 */
@Component
public class ClienteRepositoryAdapter implements ClienteRepository {

    private final ClienteJpaRepository jpaRepository;

    public ClienteRepositoryAdapter(ClienteJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Cliente save(Cliente cliente) {
        var salvo = jpaRepository.save(ClienteEntityMapper.toJpaEntity(cliente));
        return ClienteEntityMapper.toDomain(salvo);
    }

    @Override
    public Optional<Cliente> findById(UUID id) {
        return jpaRepository.findById(id).map(ClienteEntityMapper::toDomain);
    }

    @Override
    public Optional<Cliente> findByCpf(Cpf cpf) {
        return jpaRepository.findByCpf(cpf.valor()).map(ClienteEntityMapper::toDomain);
    }

    @Override
    public boolean existsByCpf(Cpf cpf) {
        return jpaRepository.existsByCpf(cpf.valor());
    }

    @Override
    public List<Cliente> findAll() {
        return jpaRepository.findAll().stream().map(ClienteEntityMapper::toDomain).toList();
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }
}
