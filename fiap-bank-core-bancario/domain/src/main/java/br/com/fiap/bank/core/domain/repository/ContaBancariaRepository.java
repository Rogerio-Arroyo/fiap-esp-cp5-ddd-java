package br.com.fiap.bank.core.domain.repository;

import br.com.fiap.bank.core.domain.model.ContaBancaria;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Porta (interface) de repositorio do agregado Conta Bancaria, totalmente
 * desacoplada da infraestrutura e dos frameworks de persistencia. As
 * implementacoes concretas (adapters) residem exclusivamente no modulo infrastructure.
 */
public interface ContaBancariaRepository {

    ContaBancaria save(ContaBancaria conta);

    Optional<ContaBancaria> findById(UUID id);

    Optional<ContaBancaria> findByNumero(String numero);

    List<ContaBancaria> findByClienteId(UUID clienteId);

    List<ContaBancaria> findByClienteCpf(String cpf);

    List<ContaBancaria> findAll();

    boolean existsByNumero(String numero);
}
