package br.com.fiap.bank.plataforma.domain.port;

import br.com.fiap.bank.plataforma.domain.model.vo.HistoricoCrmInfo;

/**
 * Porta (interface) de integracao com o CRM corporativo, usada pela Plataforma
 * de Credito para consultar o historico de relacionamento do cliente durante
 * a orquestracao da analise de risco.
 */
public interface CrmPort {

    HistoricoCrmInfo consultarHistorico(String cpf);
}
