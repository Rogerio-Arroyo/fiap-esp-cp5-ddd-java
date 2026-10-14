package br.com.fiap.bank.plataforma.application.service;

import br.com.fiap.bank.plataforma.application.dto.DecisaoOperadorRequest;
import br.com.fiap.bank.plataforma.application.dto.SolicitacaoCreditoResponse;
import br.com.fiap.bank.plataforma.application.dto.SolicitarCreditoRequest;
import br.com.fiap.bank.plataforma.domain.exception.SolicitacaoCreditoNaoEncontradaException;
import br.com.fiap.bank.plataforma.domain.model.SolicitacaoCredito;
import br.com.fiap.bank.plataforma.domain.model.vo.Cpf;
import br.com.fiap.bank.plataforma.domain.port.BureauCreditoPort;
import br.com.fiap.bank.plataforma.domain.port.CoreBancarioPort;
import br.com.fiap.bank.plataforma.domain.port.CrmPort;
import br.com.fiap.bank.plataforma.domain.port.NotificacaoPort;
import br.com.fiap.bank.plataforma.domain.port.ScrPort;
import br.com.fiap.bank.plataforma.domain.repository.SolicitacaoCreditoRepository;
import br.com.fiap.bank.plataforma.domain.service.MotorDeCreditoDomainService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Servico de aplicacao (caso de uso) responsavel por orquestrar a jornada
 * completa de uma proposta de credito: aciona o Motor de Credito (dominio puro)
 * consultando as Portas de integracao externa (SCR, Bureau, CRM, Core Bancario,
 * Notificacoes), delegando toda regra de negocio ao dominio.
 */
@Service
public class SolicitacaoCreditoService {

    private final SolicitacaoCreditoRepository repository;
    private final ScrPort scrPort;
    private final BureauCreditoPort bureauCreditoPort;
    private final CrmPort crmPort;
    private final CoreBancarioPort coreBancarioPort;
    private final NotificacaoPort notificacaoPort;

    public SolicitacaoCreditoService(SolicitacaoCreditoRepository repository, ScrPort scrPort,
                                      BureauCreditoPort bureauCreditoPort, CrmPort crmPort,
                                      CoreBancarioPort coreBancarioPort, NotificacaoPort notificacaoPort) {
        this.repository = repository;
        this.scrPort = scrPort;
        this.bureauCreditoPort = bureauCreditoPort;
        this.crmPort = crmPort;
        this.coreBancarioPort = coreBancarioPort;
        this.notificacaoPort = notificacaoPort;
    }

    @Transactional
    public SolicitacaoCreditoResponse solicitar(SolicitarCreditoRequest request) {
        Cpf cpf = Cpf.of(request.cpfCliente());

        SolicitacaoCredito solicitacao = SolicitacaoCredito.nova(cpf, request.nomeCliente(), request.tipoCredito(),
                request.valorSolicitado(), request.quantidadeParcelas(), request.rendaMensal());

        // 1. Consulta SCR (Sistema do Banco Central) - nivel de endividamento global
        boolean temRestricaoScr = scrPort.consultarRestricoes(cpf.valor());

        // 2. Consulta Bureau de Credito - score de risco
        int scoreBureau = bureauCreditoPort.consultarScore(cpf.valor());

        // 3. Consulta CRM corporativo - historico de relacionamento (orquestracao concomitante)
        crmPort.consultarHistorico(cpf.valor());

        // 4. Consulta Core Bancario - valida movimentacoes e saldo do cliente
        coreBancarioPort.consultarConta(cpf.valor());

        // 5. Motor de Credito (dominio puro) decide o resultado da analise
        MotorDeCreditoDomainService.Decisao decisao = MotorDeCreditoDomainService.avaliar(solicitacao, temRestricaoScr, scoreBureau);
        solicitacao.registrarAvaliacaoAutomatica(temRestricaoScr, scoreBureau, decisao.status(), decisao.parecer());

        SolicitacaoCredito salva = repository.save(solicitacao);

        // 6. Aciona o Sistema de Notificacoes externo, comunicando o andamento ao cliente
        notificacaoPort.enviar(cpf.valor(), "Sua solicitacao de credito #" + salva.getId() + " esta no status: " + salva.getStatus());

        return SolicitacaoCreditoResponse.fromDomain(salva);
    }

    @Transactional(readOnly = true)
    public SolicitacaoCreditoResponse buscarPorId(Long id) {
        return SolicitacaoCreditoResponse.fromDomain(buscarEntidadePorId(id));
    }

    @Transactional(readOnly = true)
    public List<SolicitacaoCreditoResponse> buscarPorCpfCliente(String cpf) {
        return repository.findByCpfCliente(Cpf.of(cpf).valor()).stream()
                .map(SolicitacaoCreditoResponse::fromDomain)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<SolicitacaoCreditoResponse> listarTodas() {
        return repository.findAll().stream().map(SolicitacaoCreditoResponse::fromDomain).toList();
    }

    @Transactional
    public SolicitacaoCreditoResponse registrarDecisaoOperador(Long id, DecisaoOperadorRequest request) {
        SolicitacaoCredito solicitacao = buscarEntidadePorId(id);
        solicitacao.registrarDecisaoOperador(request.status(), request.observacao());
        SolicitacaoCredito salva = repository.save(solicitacao);

        String resultado = salva.getStatus().name();
        notificacaoPort.enviar(salva.getCpfCliente().valor(),
                "Sua solicitacao de credito #" + salva.getId() + " foi " + resultado + " pela Mesa de Operacoes.");

        return SolicitacaoCreditoResponse.fromDomain(salva);
    }

    private SolicitacaoCredito buscarEntidadePorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new SolicitacaoCreditoNaoEncontradaException("Solicitacao de credito nao encontrada com ID: " + id));
    }
}
