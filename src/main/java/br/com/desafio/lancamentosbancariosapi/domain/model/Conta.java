package br.com.desafio.lancamentosbancariosapi.domain.model;

import br.com.desafio.lancamentosbancariosapi.domain.exception.SaldoInsuficienteException;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Table(name = "CONTAS")
@Getter
@Setter
public class Conta {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "conta_seq")
    @SequenceGenerator(name = "conta_seq", sequenceName = "SEQ_CONTA", allocationSize = 1)
    private Long id;

    @Column(name = "NUMERO_CONTA", unique = true, nullable = false)
    private String numeroConta;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal saldo;

    @Version // REQUISITO 3 e 4: thread-safety
    private Long version;

    protected Conta() {
        this.numeroConta = null;
    }

    public Conta(String numeroConta, BigDecimal saldoInicial) {
        this.numeroConta = Objects.requireNonNull(numeroConta, "Numero da conta é obrigatorio");
        this.saldo = saldoInicial != null ? saldoInicial : BigDecimal.ZERO;
    }

    /**
     * Metodo central do dominio
     * Atende ao requisito de thread-safety quando usado com @Version
     */
    public void aplicarLancamento(Lancamento lancamento) {
        Objects.requireNonNull(lancamento, "Lancamento nao pode ser nulo");
        validarSaldoParaDebito(lancamento);
        this.saldo = lancamento.aplicar(this.saldo);
    }

    private void validarSaldoParaDebito(Lancamento lancamento) {
        if (lancamento.isDebito() && saldo.compareTo(lancamento.getValor()) < 0) {
            throw new SaldoInsuficienteException(numeroConta);
        }
    }
}