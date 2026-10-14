package br.com.fiap.bank.plataforma.application.service;

import br.com.fiap.bank.plataforma.application.dto.ContaClienteResponse;
import br.com.fiap.bank.plataforma.domain.model.vo.Cpf;
import br.com.fiap.bank.plataforma.domain.port.CoreBancarioPort;
import org.springframework.stereotype.Service;

/**
 * Servico de aplicacao responsavel pela consulta (proxy) de dados de conta do
 * cliente diretamente no Core Bancario, usado tanto pelo recurso REST de
 * conveniencia /api/v1/clientes/{cpf}/conta quanto pelo adaptador legado
 * /api/credito/consultarClienteCore.
 */
@Service
public class ConsultaCoreBancarioService {

    private final CoreBancarioPort coreBancarioPort;

    public ConsultaCoreBancarioService(CoreBancarioPort coreBancarioPort) {
        this.coreBancarioPort = coreBancarioPort;
    }

    public ContaClienteResponse consultarConta(String cpf) {
        return ContaClienteResponse.fromDomain(coreBancarioPort.consultarConta(Cpf.of(cpf).valor()));
    }
}
