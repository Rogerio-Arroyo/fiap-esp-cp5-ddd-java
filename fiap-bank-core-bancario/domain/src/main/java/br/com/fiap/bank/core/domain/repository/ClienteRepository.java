package br.com.fiap.bank.core.domain.repository;

import br.com.fiap.bank.core.domain.model.Cliente;
import br.com.fiap.bank.core.domain.model.vo.Cpf;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Porta (interface) de repositorio do agregado Cliente, totalmente desacoplada
 * da infraestrutura e dos frameworks de persistencia. As implementacoes
 * concretas (adapters) residem exclusivamente no modulo infrastructure.
 */
public interface ClienteRepository {

    Cliente save(Cliente cliente);

    Optional<Cliente> findById(UUID id);

    Optional<Cliente> findByCpf(Cpf cpf);

    boolean existsByCpf(Cpf cpf);

    List<Cliente> findAll();

    void deleteById(UUID id);
}
