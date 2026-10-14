package br.com.fiap.bank.plataforma.domain.port;

import br.com.fiap.bank.plataforma.domain.model.vo.ContaClienteInfo;

/**
 * Porta (interface) de integracao com o Core Bancario, usada pelo Motor de
 * Credito para validar movimentacoes e saldos do cliente antes da concessao.
 */
public interface CoreBancarioPort {

    ContaClienteInfo consultarConta(String cpf);
}
