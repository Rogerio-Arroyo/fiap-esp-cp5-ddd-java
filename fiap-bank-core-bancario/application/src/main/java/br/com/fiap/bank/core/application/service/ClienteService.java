package br.com.fiap.bank.core.application.service;

import br.com.fiap.bank.core.application.dto.AtualizarClienteRequest;
import br.com.fiap.bank.core.application.dto.ClienteResponse;
import br.com.fiap.bank.core.application.dto.CriarClienteRequest;
import br.com.fiap.bank.core.domain.exception.ClienteNaoEncontradoException;
import br.com.fiap.bank.core.domain.exception.CpfJaCadastradoException;
import br.com.fiap.bank.core.domain.model.Cliente;
import br.com.fiap.bank.core.domain.model.vo.Cpf;
import br.com.fiap.bank.core.domain.model.vo.Email;
import br.com.fiap.bank.core.domain.repository.ClienteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Servico de aplicacao (caso de uso) responsavel por orquestrar o ciclo de
 * vida dos Clientes correntistas, delegando as regras de negocio puras para
 * a entidade de dominio Cliente.
 */
@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;

    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    @Transactional
    public ClienteResponse criar(CriarClienteRequest request) {
        Cpf cpf = Cpf.of(request.cpf());
        if (clienteRepository.existsByCpf(cpf)) {
            throw new CpfJaCadastradoException("Ja existe um cliente cadastrado com o CPF " + cpf.formatado());
        }
        Cliente cliente = Cliente.novo(cpf, request.nome(), Email.of(request.email()),
                request.dataNascimento(), request.rendaMensal());
        return ClienteResponse.fromDomain(clienteRepository.save(cliente));
    }

    @Transactional(readOnly = true)
    public ClienteResponse buscarPorId(UUID id) {
        return ClienteResponse.fromDomain(buscarEntidadePorId(id));
    }

    @Transactional(readOnly = true)
    public ClienteResponse buscarPorCpf(String cpf) {
        Cliente cliente = clienteRepository.findByCpf(Cpf.of(cpf))
                .orElseThrow(() -> new ClienteNaoEncontradoException("Cliente nao encontrado para o CPF informado: " + cpf));
        return ClienteResponse.fromDomain(cliente);
    }

    @Transactional(readOnly = true)
    public List<ClienteResponse> listarTodos() {
        return clienteRepository.findAll().stream().map(ClienteResponse::fromDomain).toList();
    }

    @Transactional
    public ClienteResponse atualizar(UUID id, AtualizarClienteRequest request) {
        Cliente cliente = buscarEntidadePorId(id);
        cliente.atualizarDados(request.nome(), Email.of(request.email()), request.rendaMensal());
        return ClienteResponse.fromDomain(clienteRepository.save(cliente));
    }

    @Transactional
    public void remover(UUID id) {
        buscarEntidadePorId(id);
        clienteRepository.deleteById(id);
    }

    private Cliente buscarEntidadePorId(UUID id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new ClienteNaoEncontradoException("Cliente nao encontrado para o ID: " + id));
    }
}
