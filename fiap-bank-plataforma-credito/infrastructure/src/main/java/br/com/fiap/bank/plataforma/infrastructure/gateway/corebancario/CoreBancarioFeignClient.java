package br.com.fiap.bank.plataforma.infrastructure.gateway.corebancario;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * Client Spring Cloud OpenFeign que consome o recurso RESTful oficial
 * /api/v1/contas do microsservico Core Bancario (ver Secao 9 do Checkpoint).
 */
@FeignClient(name = "coreBancarioClient", url = "${core.bancario.url:http://localhost:8080}")
public interface CoreBancarioFeignClient {

    @GetMapping("/api/v1/contas/cliente/{cpf}")
    List<CoreContaDTO> buscarContasPorCpf(@PathVariable("cpf") String cpf);
}
