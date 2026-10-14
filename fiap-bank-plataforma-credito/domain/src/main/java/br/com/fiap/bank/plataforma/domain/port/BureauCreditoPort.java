package br.com.fiap.bank.plataforma.domain.port;

/**
 * Porta (interface) de integracao com o Bureau de Credito (ex.: Serasa/SPC),
 * usada pelo Motor de Credito para obter o Score de Risco do cliente (0-1000).
 */
public interface BureauCreditoPort {

    int consultarScore(String cpf);
}
