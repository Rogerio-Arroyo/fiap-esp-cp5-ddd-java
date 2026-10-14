package br.com.fiap.bank.plataforma.infrastructure.gateway.corebancario;

import br.com.fiap.bank.plataforma.domain.model.vo.ContaClienteInfo;
import br.com.fiap.bank.plataforma.domain.port.CoreBancarioPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Adaptador que implementa a porta de dominio CoreBancarioPort, consumindo
 * o Core Bancario via Spring Cloud OpenFeign. Em caso de indisponibilidade do
 * Core Bancario ou de ausencia de conta vinculada ao CPF, degrada graciosamente
 * devolvendo uma representacao padrao (ContaClienteInfo.indisponivel()), preservando
 * a alta disponibilidade da Plataforma de Credito mesmo sob falha de dependencia externa.
 */
@Component
public class CoreBancarioAdapter implements CoreBancarioPort {

    private static final Logger log = LoggerFactory.getLogger(CoreBancarioAdapter.class);

    private final CoreBancarioFeignClient feignClient;

    public CoreBancarioAdapter(CoreBancarioFeignClient feignClient) {
        this.feignClient = feignClient;
    }

    @Override
    public ContaClienteInfo consultarConta(String cpf) {
        try {
            List<CoreContaDTO> contas = feignClient.buscarContasPorCpf(cpf);
            if (contas == null || contas.isEmpty()) {
                log.warn("[CORE BANCARIO CLIENT] Nenhuma conta encontrada no Core Bancario para o CPF {}. Usando dados padrao.", cpf);
                return ContaClienteInfo.indisponivel();
            }
            CoreContaDTO conta = contas.getFirst();
            return new ContaClienteInfo(conta.status(), conta.saldo(), conta.agencia(), conta.numero());
        } catch (Exception e) {
            log.warn("[CORE BANCARIO CLIENT] Core Bancario indisponivel para o CPF {}: {}. Usando dados padrao.", cpf, e.getMessage());
            return ContaClienteInfo.indisponivel();
        }
    }
}
