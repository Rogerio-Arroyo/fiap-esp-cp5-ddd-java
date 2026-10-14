package br.com.fiap.bank.plataforma.domain.repository;

import br.com.fiap.bank.plataforma.domain.model.SolicitacaoCredito;

import java.util.List;
import java.util.Optional;

/**
 * Porta (interface) de repositorio do agregado Solicitacao de Credito,
 * totalmente desacoplada da infraestrutura e dos frameworks de persistencia.
 * As implementacoes concretas (adapters) residem exclusivamente no modulo infrastructure.
 */
public interface SolicitacaoCreditoRepository {

    SolicitacaoCredito save(SolicitacaoCredito solicitacao);

    Optional<SolicitacaoCredito> findById(Long id);

    List<SolicitacaoCredito> findByCpfCliente(String cpf);

    List<SolicitacaoCredito> findAll();
}
