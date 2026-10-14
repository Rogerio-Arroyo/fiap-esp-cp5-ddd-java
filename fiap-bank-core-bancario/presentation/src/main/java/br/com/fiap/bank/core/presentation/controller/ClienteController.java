package br.com.fiap.bank.core.presentation.controller;

import br.com.fiap.bank.core.application.dto.AtualizarClienteRequest;
import br.com.fiap.bank.core.application.dto.ClienteResponse;
import br.com.fiap.bank.core.application.dto.CriarClienteRequest;
import br.com.fiap.bank.core.application.service.ClienteService;
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
import java.util.UUID;

/**
 * Controller RESTful que expoe o recurso canonico /api/v1/clientes, conforme a
 * Matriz de Endpoints da API RESTful do Core Bancario (Checkpoint 5, secao 10.1).
 */
@RestController
@RequestMapping("/api/v1/clientes")
public class ClienteController {

    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @GetMapping
    public ResponseEntity<List<ClienteResponse>> listarTodos() {
        return ResponseEntity.ok(clienteService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClienteResponse> buscarPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(clienteService.buscarPorId(id));
    }

    @GetMapping("/cpf/{cpf}")
    public ResponseEntity<ClienteResponse> buscarPorCpf(@PathVariable String cpf) {
        return ResponseEntity.ok(clienteService.buscarPorCpf(cpf));
    }

    @PostMapping
    public ResponseEntity<ClienteResponse> criar(@Valid @RequestBody CriarClienteRequest request,
                                                  UriComponentsBuilder uriBuilder) {
        ClienteResponse criado = clienteService.criar(request);
        var location = uriBuilder.path("/api/v1/clientes/{id}").buildAndExpand(criado.id()).toUri();
        return ResponseEntity.created(location).body(criado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClienteResponse> atualizar(@PathVariable UUID id,
                                                       @Valid @RequestBody AtualizarClienteRequest request) {
        return ResponseEntity.ok(clienteService.atualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable UUID id) {
        clienteService.remover(id);
        return ResponseEntity.noContent().build();
    }
}
