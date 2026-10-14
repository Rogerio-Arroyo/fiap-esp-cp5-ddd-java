package br.com.fiap.bank.plataforma.infrastructure.persistence.adapter;

import br.com.fiap.bank.plataforma.domain.model.SolicitacaoCredito;
import br.com.fiap.bank.plataforma.domain.repository.SolicitacaoCreditoRepository;
import br.com.fiap.bank.plataforma.infrastructure.persistence.mapper.SolicitacaoCreditoEntityMapper;
import br.com.fiap.bank.plataforma.infrastructure.persistence.repository.SolicitacaoCreditoJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Adaptador de persistencia (Repository Adapter) que implementa a porta de
 * dominio SolicitacaoCreditoRepository usando Spring Data JPA.
 */
@Component
public class SolicitacaoCreditoRepositoryAdapter implements SolicitacaoCreditoRepository {

    private final SolicitacaoCreditoJpaRepository jpaRepository;

    public SolicitacaoCreditoRepositoryAdapter(SolicitacaoCreditoJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public SolicitacaoCredito save(SolicitacaoCredito solicitacao) {
        var salva = jpaRepository.save(SolicitacaoCreditoEntityMapper.toJpaEntity(solicitacao));
        return SolicitacaoCreditoEntityMapper.toDomain(salva);
    }

    @Override
    public Optional<SolicitacaoCredito> findById(Long id) {
        return jpaRepository.findById(id).map(SolicitacaoCreditoEntityMapper::toDomain);
    }

    @Override
    public List<SolicitacaoCredito> findByCpfCliente(String cpf) {
        return jpaRepository.findByCpfClienteOrderByDataSolicitacaoDesc(cpf).stream()
                .map(SolicitacaoCreditoEntityMapper::toDomain)
                .toList();
    }

    @Override
    public List<SolicitacaoCredito> findAll() {
        return jpaRepository.findAllByOrderByDataSolicitacaoDesc().stream()
                .map(SolicitacaoCreditoEntityMapper::toDomain)
                .toList();
    }
}
