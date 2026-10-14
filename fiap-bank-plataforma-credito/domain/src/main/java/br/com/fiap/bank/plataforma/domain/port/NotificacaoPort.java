package br.com.fiap.bank.plataforma.domain.port;

/**
 * Porta (interface) de integracao com o Sistema de Notificacoes externo,
 * usada para comunicar ao cliente o andamento da sua proposta de credito.
 */
public interface NotificacaoPort {

    void enviar(String cpf, String mensagem);
}
