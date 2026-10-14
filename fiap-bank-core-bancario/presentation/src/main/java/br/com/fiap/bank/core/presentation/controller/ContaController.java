package br.com.fiap.bank.core.presentation.controller;

import br.com.fiap.bank.core.application.dto.AtualizarSaldoRequest;
import br.com.fiap.bank.core.application.dto.ContaResponse;
import br.com.fiap.bank.core.application.dto.CriarContaRequest;
import br.com.fiap.bank.core.application.service.ContaBancariaService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

/**
 * Controller RESTful que expoe o recurso canonico /api/v1/contas, conforme a
 * Matriz de Endpoints da API RESTful do Core Bancario (Checkpoint 5, secao 10.1).
 * <p>
 * As operacoes de consulta, movimentacao de saldo e inativacao utilizam o
 * numero da conta (chave de negocio) como identificador na URI, conforme
 * descrito na especificacao textual da secao 9 do Checkpoint.
 */
@RestController
@RequestMapping("/api/v1/contas")
public class ContaController {

    private final ContaBancariaService contaService;

    public ContaController(ContaBancariaService contaService) {
        this.contaService = contaService;
    }

    @GetMapping
    public ResponseEntity<List<ContaResponse>> listarTodas() {
        return ResponseEntity.ok(contaService.listarTodas());
    }

    @GetMapping("/{numeroConta}")
    public ResponseEntity<ContaResponse> buscarPorNumero(@PathVariable String numeroConta) {
        return ResponseEntity.ok(contaService.buscarPorNumero(numeroConta));
    }

    @GetMapping("/cliente/{cpf}")
    public ResponseEntity<List<ContaResponse>> buscarPorCpfCliente(@PathVariable String cpf) {
        return ResponseEntity.ok(contaService.buscarPorCpfCliente(cpf));
    }

    @PostMapping
    public ResponseEntity<ContaResponse> criar(@Valid @RequestBody CriarContaRequest request,
                                                UriComponentsBuilder uriBuilder) {
        ContaResponse criada = contaService.criar(request);
        var location = uriBuilder.path("/api/v1/contas/{numeroConta}").buildAndExpand(criada.numero()).toUri();
        return ResponseEntity.created(location).body(criada);
    }

    @PutMapping("/{numeroConta}/saldo")
    public ResponseEntity<ContaResponse> atualizarSaldo(@PathVariable String numeroConta,
                                                          @Valid @RequestBody AtualizarSaldoRequest request) {
        return ResponseEntity.ok(contaService.atualizarSaldo(numeroConta, request));
    }

    @DeleteMapping("/{numeroConta}")
    public ResponseEntity<Void> inativar(@PathVariable String numeroConta) {
        contaService.inativar(numeroConta);
        return ResponseEntity.noContent().build();
    }
}
