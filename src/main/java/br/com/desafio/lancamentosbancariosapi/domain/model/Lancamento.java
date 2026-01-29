package br.com.desafio.lancamentosbancariosapi.domain.model;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Representa um lançamento individual de crédito ou débito.
 * Pode ser usado como um Value Object dentro da entidade Conta ou
 * como uma entidade separada para histórico.
 */
@Getter
@Setter
public class Lancamento {

    private final BigDecimal valor;
    private final TipoLancamento tipo;

    public Lancamento(BigDecimal valor, TipoLancamento tipo) {
        this.valor = Objects.requireNonNull(valor, "Valor é obrigatorio");
        this.tipo = Objects.requireNonNull(tipo, "Tipo de lancamento é obrigatorio");

        if (valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("O valor do lancamento deve ser maior que zero");
        }
    }

    public boolean isDebito() {
        return TipoLancamento.DEBITO.equals(this.tipo);
    }

    public boolean isCredito() {
        return TipoLancamento.CREDITO.equals(this.tipo);
    }

    /**
     * Aplica a lógica matemática baseada no tipo de lancamento
     * @param saldoAtual O saldo antes da operacao
     * @return O novo saldo calculado
     */
    public BigDecimal aplicar(BigDecimal saldoAtual) {
        if (isCredito()) {
            return saldoAtual.add(this.valor);
        } else {
            return saldoAtual.subtract(this.valor);
        }
    }

}