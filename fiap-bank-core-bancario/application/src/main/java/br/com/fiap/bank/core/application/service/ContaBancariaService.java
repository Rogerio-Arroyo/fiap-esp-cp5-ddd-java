package br.com.fiap.bank.core.application.service;

import br.com.fiap.bank.core.application.dto.AtualizarSaldoRequest;
import br.com.fiap.bank.core.application.dto.ContaResponse;
import br.com.fiap.bank.core.application.dto.CriarContaRequest;
import br.com.fiap.bank.core.domain.exception.ClienteNaoEncontradoException;
import br.com.fiap.bank.core.domain.exception.ContaBancariaNaoEncontradaException;
import br.com.fiap.bank.core.domain.model.Cliente;
import br.com.fiap.bank.core.domain.model.ContaBancaria;
import br.com.fiap.bank.core.domain.model.vo.Cpf;
import br.com.fiap.bank.core.domain.repository.ClienteRepository;
import br.com.fiap.bank.core.domain.repository.ContaBancariaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.List;
import java.util.UUID;

/**
 * Servico de aplicacao (caso de uso) responsavel por orquestrar o ciclo de
 * vida das Contas Bancarias e suas movimentacoes financeiras atomicas,
 * delegando as regras de saldo e status para a entidade de dominio ContaBancaria.
 */
@Service
public class ContaBancariaService {

    private static final SecureRandom RANDOM = new SecureRandom();

    private final ContaBancariaRepository contaRepository;
    private final ClienteRepository clienteRepository;

    public ContaBancariaService(ContaBancariaRepository contaRepository, ClienteRepository clienteRepository) {
        this.contaRepository = contaRepository;
        this.clienteRepository = clienteRepository;
    }

    @Transactional
    public ContaResponse criar(CriarContaRequest request) {
        Cliente titular = clienteRepository.findByCpf(Cpf.of(request.cpfCliente()))
                .orElseThrow(() -> new ClienteNaoEncontradoException(
                        "Nao e possivel abrir conta: cliente nao encontrado para o CPF " + request.cpfCliente()));

        ContaBancaria conta = ContaBancaria.abrir(gerarNumeroUnico(), request.agenciaOuPadrao(),
                request.tipoConta(), titular.getId(), request.saldoInicial());

        ContaBancaria salva = contaRepository.save(conta);
        return ContaResponse.fromDomain(salva, titular);
    }

    @Transactional(readOnly = true)
    public ContaResponse buscarPorId(UUID id) {
        return montarResponse(buscarEntidadePorId(id));
    }

    @Transactional(readOnly = true)
    public ContaResponse buscarPorNumero(String numero) {
        return montarResponse(buscarEntidadePorNumero(numero));
    }

    @Transactional(readOnly = true)
    public List<ContaResponse> buscarPorCpfCliente(String cpf) {
        return contaRepository.findByClienteCpf(Cpf.of(cpf).valor()).stream()
                .map(this::montarResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ContaResponse> listarTodas() {
        return contaRepository.findAll().stream().map(this::montarResponse).toList();
    }

    @Transactional
    public ContaResponse atualizarSaldo(String numero, AtualizarSaldoRequest request) {
        ContaBancaria conta = buscarEntidadePorNumero(numero);
        if (request.isDeposito()) {
            conta.depositar(request.valor());
        } else {
            conta.sacar(request.valor());
        }
        return montarResponse(contaRepository.save(conta));
    }

    @Transactional
    public void inativar(String numero) {
        ContaBancaria conta = buscarEntidadePorNumero(numero);
        conta.encerrar();
        contaRepository.save(conta);
    }

    private ContaBancaria buscarEntidadePorId(UUID id) {
        return contaRepository.findById(id)
                .orElseThrow(() -> new ContaBancariaNaoEncontradaException("Conta bancaria nao encontrada para o ID: " + id));
    }

    private ContaBancaria buscarEntidadePorNumero(String numero) {
        return contaRepository.findByNumero(numero)
                .orElseThrow(() -> new ContaBancariaNaoEncontradaException("Conta bancaria nao encontrada para o numero: " + numero));
    }

    private ContaResponse montarResponse(ContaBancaria conta) {
        Cliente titular = clienteRepository.findById(conta.getClienteId())
                .orElseThrow(() -> new ClienteNaoEncontradoException("Titular nao encontrado para a conta " + conta.getNumero()));
        return ContaResponse.fromDomain(conta, titular);
    }

    /** Gera um numero de conta unico no formato NNNNNN-D (6 digitos sequenciais aleatorios + digito verificador mod 11). */
    private String gerarNumeroUnico() {
        String numero;
        do {
            int base = 100000 + RANDOM.nextInt(900000);
            int digitoVerificador = base % 11;
            numero = base + "-" + digitoVerificador;
        } while (contaRepository.existsByNumero(numero));
        return numero;
    }
}
