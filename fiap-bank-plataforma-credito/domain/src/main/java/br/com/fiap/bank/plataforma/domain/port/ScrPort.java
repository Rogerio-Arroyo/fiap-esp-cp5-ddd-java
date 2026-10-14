package br.com.fiap.bank.plataforma.domain.port;

/**
 * Porta (interface) de integracao com o Sistema de Informacoes de Creditos do
 * Banco Central (SCR), usada pelo Motor de Credito para verificar o nivel de
 * endividamento global do cliente em outras instituicoes financeiras.
 */
public interface ScrPort {

    /** Verifica se o cliente possui restricoes ou dividas vencidas registradas no SCR. */
    boolean consultarRestricoes(String cpf);
}
