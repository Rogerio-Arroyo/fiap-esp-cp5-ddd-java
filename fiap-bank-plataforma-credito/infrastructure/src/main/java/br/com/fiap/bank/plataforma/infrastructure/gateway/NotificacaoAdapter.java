package br.com.fiap.bank.plataforma.infrastructure.gateway;

import br.com.fiap.bank.plataforma.domain.port.NotificacaoPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Adaptador simulado do Sistema de Notificacoes externo (disparo de e-mail/SMS),
 * acionado de forma concomitante ao processamento da proposta de credito.
 */
@Component
public class NotificacaoAdapter implements NotificacaoPort {

    private static final Logger log = LoggerFactory.getLogger(NotificacaoAdapter.class);

    @Override
    public void enviar(String cpf, String mensagem) {
        log.info("[SISTEMA DE NOTIFICACOES] Notificacao enviada para CPF {}: {}", cpf, mensagem);
    }
}
