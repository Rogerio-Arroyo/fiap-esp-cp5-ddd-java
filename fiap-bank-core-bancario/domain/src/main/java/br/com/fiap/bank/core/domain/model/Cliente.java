package br.com.fiap.bank.core.domain.model;

import br.com.fiap.bank.core.domain.model.vo.Cpf;
import br.com.fiap.bank.core.domain.model.vo.Email;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

/**
 * Entidade de dominio pura que representa um Cliente (correntista) do FIAP Bank.
 * Nao possui nenhuma anotacao de persistencia ou dependencia de framework:
 * encapsula exclusivamente as regras de negocio e invariantes do correntista.
 */
@Getter
public class Cliente {

    private final UUID id;
    private final Cpf cpf;
    private String nome;
    private Email email;
    private final LocalDate dataNascimento;
    private BigDecimal rendaMensal;
    private final LocalDate dataCadastro;

    private Cliente(UUID id, Cpf cpf, String nome, Email email, LocalDate dataNascimento,
                     BigDecimal rendaMensal, LocalDate dataCadastro) {
        this.id = id;
        this.cpf = cpf;
        this.dataNascimento = dataNascimento;
        this.dataCadastro = dataCadastro;
        atualizarDados(nome, email, rendaMensal);
    }

    /** Factory de criacao de um novo Cliente, validando as invariantes de cadastro. */
    public static Cliente novo(Cpf cpf, String nome, Email email, LocalDate dataNascimento, BigDecimal rendaMensal) {
        validarDataNascimento(dataNascimento);
        return new Cliente(UUID.randomUUID(), cpf, nome, email, dataNascimento, rendaMensal, LocalDate.now());
    }

    /** Factory de reconstrucao a partir de dados ja persistidos (usada pelos adaptadores de infraestrutura). */
    public static Cliente restaurar(UUID id, Cpf cpf, String nome, Email email, LocalDate dataNascimento,
                                     BigDecimal rendaMensal, LocalDate dataCadastro) {
        return new Cliente(id, cpf, nome, email, dataNascimento, rendaMensal, dataCadastro);
    }

    /** Atualiza os dados cadastrais do cliente, preservando CPF e data de nascimento como identidade imutavel. */
    public void atualizarDados(String nome, Email email, BigDecimal rendaMensal) {
        validarNome(nome);
        validarRendaMensal(rendaMensal);
        this.nome = nome.trim();
        this.email = Objects.requireNonNull(email, "O e-mail do cliente e obrigatorio.");
        this.rendaMensal = rendaMensal;
    }

    private static void validarNome(String nome) {
        if (nome == null || nome.trim().length() < 3 || nome.trim().length() > 150) {
            throw new IllegalArgumentException("O nome do cliente deve ter entre 3 e 150 caracteres.");
        }
    }

    private static void validarRendaMensal(BigDecimal rendaMensal) {
        if (rendaMensal == null || rendaMensal.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("A renda mensal do cliente nao pode ser negativa.");
        }
    }

    private static void validarDataNascimento(LocalDate dataNascimento) {
        if (dataNascimento == null || !dataNascimento.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("A data de nascimento deve ser uma data valida no passado.");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Cliente cliente)) return false;
        return id.equals(cliente.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
