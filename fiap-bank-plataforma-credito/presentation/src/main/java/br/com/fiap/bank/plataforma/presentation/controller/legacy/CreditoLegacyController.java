package br.com.fiap.bank.plataforma.presentation.controller.legacy;

import br.com.fiap.bank.plataforma.application.dto.ContaClienteResponse;
import br.com.fiap.bank.plataforma.application.dto.DecisaoOperadorRequest;
import br.com.fiap.bank.plataforma.application.dto.SolicitacaoCreditoResponse;
import br.com.fiap.bank.plataforma.application.dto.SolicitarCreditoRequest;
import br.com.fiap.bank.plataforma.application.service.ConsultaCoreBancarioService;
import br.com.fiap.bank.plataforma.application.service.SolicitacaoCreditoService;
import br.com.fiap.bank.plataforma.domain.model.vo.StatusSolicitacao;
import br.com.fiap.bank.plataforma.presentation.controller.legacy.dto.AprovarSolicitacaoRequest;
import br.com.fiap.bank.plataforma.presentation.controller.legacy.dto.BuscarSolicitacaoRequest;
import br.com.fiap.bank.plataforma.presentation.controller.legacy.dto.ConsultarStatusRequest;
import br.com.fiap.bank.plataforma.presentation.controller.legacy.dto.ReprovarSolicitacaoRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controller Adaptador (Adapter) de RETROCOMPATIBILIDADE.
 * <p>
 * Preserva EXATAMENTE as rotas legadas de acao (Non-RESTful, {@code POST} +
 * verbo na URI) que existiam na Plataforma de Credito antes da refatoracao,
 * para que as aplicacoes front-end legadas em producao (fiap-bank-mesa-operacoes-credito
 * via Spring Cloud OpenFeign, e fiap-bank-internet-banking-mobile via Angular HttpClient)
 * continuem operando sem qualquer alteracao de contrato, enquanto toda a
 * logica de negocio real passa a residir no nucleo DDD (domain/application).
 * <p>
 * Este controller NAO deve ganhar nenhuma regra de negocio nova: e uma casca
 * fina de traducao entre o contrato legado e os Casos de Uso da camada application,
 * que tambem sao reaproveitados pelo novo recurso RESTful {@code /api/v1/solicitacoes-credito}.
 *
 * @deprecated Mantido exclusivamente por retrocompatibilidade. Novas integracoes devem
 * usar o recurso RESTful {@code /api/v1/solicitacoes-credito}.
 */
@Deprecated
@RestController
@RequestMapping("/api/credito")
@CrossOrigin(origins = "*")
public class CreditoLegacyController {

    private final SolicitacaoCreditoService solicitacaoService;
    private final ConsultaCoreBancarioService consultaCoreBancarioService;

    public CreditoLegacyController(SolicitacaoCreditoService solicitacaoService,
                                    ConsultaCoreBancarioService consultaCoreBancarioService) {
        this.solicitacaoService = solicitacaoService;
        this.consultaCoreBancarioService = consultaCoreBancarioService;
    }

    @PostMapping("/solicitarCredito")
    public ResponseEntity<SolicitacaoCreditoResponse> solicitarCredito(@Valid @RequestBody SolicitarCreditoRequest request) {
        return ResponseEntity.ok(solicitacaoService.solicitar(request));
    }

    @PostMapping("/consultarStatusCredito")
    public ResponseEntity<List<SolicitacaoCreditoResponse>> consultarStatusCredito(@RequestBody ConsultarStatusRequest request) {
        return ResponseEntity.ok(solicitacaoService.buscarPorCpfCliente(request.cpfCliente()));
    }

    @PostMapping("/buscarSolicitacaoPorId")
    public ResponseEntity<SolicitacaoCreditoResponse> buscarSolicitacaoPorId(@RequestBody BuscarSolicitacaoRequest request) {
        return ResponseEntity.ok(solicitacaoService.buscarPorId(request.solicitacaoId()));
    }

    @PostMapping("/listarTodasSolicitacoes")
    public ResponseEntity<List<SolicitacaoCreditoResponse>> listarTodasSolicitacoes() {
        return ResponseEntity.ok(solicitacaoService.listarTodas());
    }

    @PostMapping("/aprovarSolicitacao")
    public ResponseEntity<SolicitacaoCreditoResponse> aprovarSolicitacao(@RequestBody AprovarSolicitacaoRequest request) {
        var decisao = new DecisaoOperadorRequest(StatusSolicitacao.APROVADO, request.parecerOperador());
        return ResponseEntity.ok(solicitacaoService.registrarDecisaoOperador(request.solicitacaoId(), decisao));
    }

    @PostMapping("/reprovarSolicitacao")
    public ResponseEntity<SolicitacaoCreditoResponse> reprovarSolicitacao(@RequestBody ReprovarSolicitacaoRequest request) {
        var decisao = new DecisaoOperadorRequest(StatusSolicitacao.REPROVADO, request.motivoReprovacao());
        return ResponseEntity.ok(solicitacaoService.registrarDecisaoOperador(request.solicitacaoId(), decisao));
    }

    @PostMapping("/consultarClienteCore")
    public ResponseEntity<ContaClienteResponse> consultarClienteCore(@RequestBody ConsultarStatusRequest request) {
        return ResponseEntity.ok(consultaCoreBancarioService.consultarConta(request.cpfCliente()));
    }
}
