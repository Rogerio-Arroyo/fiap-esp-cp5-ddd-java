package br.com.fiap.bank.plataforma.presentation.controller;

import br.com.fiap.bank.plataforma.application.dto.ContaClienteResponse;
import br.com.fiap.bank.plataforma.application.service.ConsultaCoreBancarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller RESTful de conveniencia que expoe, a partir da Plataforma de
 * Credito, uma visao somente-leitura da conta do cliente no Core Bancario
 * (recurso subordinado ao cliente, usado para enriquecer a jornada do credito).
 */
@RestController
@RequestMapping("/api/v1/clientes")
public class ClienteContaController {

    private final ConsultaCoreBancarioService consultaCoreBancarioService;

    public ClienteContaController(ConsultaCoreBancarioService consultaCoreBancarioService) {
        this.consultaCoreBancarioService = consultaCoreBancarioService;
    }

    @GetMapping("/{cpf}/conta")
    public ResponseEntity<ContaClienteResponse> consultarConta(@PathVariable String cpf) {
        return ResponseEntity.ok(consultaCoreBancarioService.consultarConta(cpf));
    }
}
