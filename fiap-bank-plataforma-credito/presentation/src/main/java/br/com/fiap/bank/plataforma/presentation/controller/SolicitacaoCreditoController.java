package br.com.fiap.bank.plataforma.presentation.controller;

import br.com.fiap.bank.plataforma.application.dto.DecisaoOperadorRequest;
import br.com.fiap.bank.plataforma.application.dto.SolicitacaoCreditoResponse;
import br.com.fiap.bank.plataforma.application.dto.SolicitarCreditoRequest;
import br.com.fiap.bank.plataforma.application.service.SolicitacaoCreditoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

/**
 * Controller RESTful que expoe o recurso canonico /api/v1/solicitacoes-credito,
 * reengenharia estritamente RESTful das antigas rotas de acao (Non-RESTful) de
 * /api/credito/* (ver CreditoLegacyController para a camada de retrocompatibilidade).
 * <p>
 * A decisao do operador da Mesa de Operacoes (aprovacao/reprovacao) e modelada
 * como uma atualizacao parcial (PATCH) do sub-recurso "status" da solicitacao,
 * em vez de verbos de acao na URI.
 */
@RestController
@RequestMapping("/api/v1/solicitacoes-credito")
public class SolicitacaoCreditoController {

    private final SolicitacaoCreditoService service;

    public SolicitacaoCreditoController(SolicitacaoCreditoService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<SolicitacaoCreditoResponse> solicitar(@Valid @RequestBody SolicitarCreditoRequest request,
                                                                  UriComponentsBuilder uriBuilder) {
        SolicitacaoCreditoResponse criada = service.solicitar(request);
        var location = uriBuilder.path("/api/v1/solicitacoes-credito/{id}").buildAndExpand(criada.id()).toUri();
        return ResponseEntity.created(location).body(criada);
    }

    @GetMapping
    public ResponseEntity<List<SolicitacaoCreditoResponse>> listarTodas() {
        return ResponseEntity.ok(service.listarTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SolicitacaoCreditoResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @GetMapping("/cliente/{cpf}")
    public ResponseEntity<List<SolicitacaoCreditoResponse>> buscarPorCpfCliente(@PathVariable String cpf) {
        return ResponseEntity.ok(service.buscarPorCpfCliente(cpf));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<SolicitacaoCreditoResponse> atualizarStatus(@PathVariable Long id,
                                                                        @Valid @RequestBody DecisaoOperadorRequest request) {
        return ResponseEntity.ok(service.registrarDecisaoOperador(id, request));
    }
}
