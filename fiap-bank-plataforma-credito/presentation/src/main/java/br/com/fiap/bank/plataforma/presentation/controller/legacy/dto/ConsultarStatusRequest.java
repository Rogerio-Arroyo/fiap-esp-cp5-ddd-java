package br.com.fiap.bank.plataforma.presentation.controller.legacy.dto;

/** Corpo de requisicao legado (formato preservado) para /api/credito/consultarStatusCredito e /consultarClienteCore. */
public record ConsultarStatusRequest(String cpfCliente) {
}
