package br.com.fiap.bank.core.domain.model;

import br.com.fiap.bank.core.domain.exception.ContaInativaException;
import br.com.fiap.bank.core.domain.model.vo.Saldo;
import br.com.fiap.bank.core.domain.model.vo.StatusConta;
import br.com.fiap.bank.core.domain.model.vo.TipoConta;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

/**
 * Entidade de dominio pura que representa uma Conta Bancaria do FIAP Bank.
 * Agregado independente de Cliente (referenciado por identificador), responsavel
 * por proteger as invariantes de saldo e de ciclo de vida da conta.
 */
@Getter
public class ContaBancaria {

    private final UUID id;
    private final String numero;
    private final String agencia;
    private final TipoConta tipoConta;
    private final UUID clienteId;
    private final LocalDate dataAbertura;
    private Saldo saldo;
    private StatusConta status;

    private ContaBancaria(UUID id, String numero, String agencia, TipoConta tipoConta, UUID clienteId,
                           LocalDate dataAbertura, Saldo saldo, StatusConta status) {
        this.id = id;
        this.numero = numero;
        this.agencia = agencia;
        this.tipoConta = tipoConta;
        this.clienteId = clienteId;
        this.dataAbertura = dataAbertura;
        this.saldo = saldo;
        this.status = status;
    }

    /** Factory de abertura de uma nova Conta Bancaria vinculada a um Cliente ja existente. */
    public static ContaBancaria abrir(String numero, String agencia, TipoConta tipoConta, UUID clienteId, BigDecimal saldoInicial) {
        Objects.requireNonNull(clienteId, "A conta deve estar vinculada a um cliente existente.");
        Objects.requireNonNull(tipoConta, "O tipo de conta e obrigatorio.");
        return new ContaBancaria(UUID.randomUUID(), numero, agencia, tipoConta, clienteId,
                LocalDate.now(), Saldo.of(saldoInicial), StatusConta.ATIVA);
    }

    /** Factory de reconstrucao a partir de dados ja persistidos (usada pelos adaptadores de infraestrutura). */
    public static ContaBancaria restaurar(UUID id, String numero, String agencia, TipoConta tipoConta, UUID clienteId,
                                           LocalDate dataAbertura, BigDecimal saldo, StatusConta status) {
        return new ContaBancaria(id, numero, agencia, tipoConta, clienteId, dataAbertura, Saldo.of(saldo), status);
    }

    /** Credita (deposita) um valor na conta. Exige que a conta esteja ATIVA. */
    public void depositar(BigDecimal valor) {
        exigirContaAtiva();
        this.saldo = this.saldo.creditar(valor);
    }

    /** Debita (saca) um valor da conta. Exige que a conta esteja ATIVA e possua saldo suficiente. */
    public void sacar(BigDecimal valor) {
        exigirContaAtiva();
        this.saldo = this.saldo.debitar(valor);
    }

    public void bloquear() {
        this.status = StatusConta.BLOQUEADA;
    }

    public void ativar() {
        this.status = StatusConta.ATIVA;
    }

    /** Encerra (inativa) o ciclo de vida da conta de forma idempotente. */
    public void encerrar() {
        this.status = StatusConta.ENCERRADA;
    }

    private void exigirContaAtiva() {
        if (this.status != StatusConta.ATIVA) {
            throw new ContaInativaException("A conta " + numero + " nao esta ATIVA (status atual: " + status + ") e nao pode ser movimentada.");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ContaBancaria that)) return false;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
