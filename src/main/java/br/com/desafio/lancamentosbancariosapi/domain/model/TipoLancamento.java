package br.com.desafio.lancamentosbancariosapi.domain.model;

import java.math.BigDecimal;

public enum TipoLancamento {

    CREDITO {
        @Override
        public BigDecimal aplicar(BigDecimal saldoAtual, BigDecimal valor) {
            return saldoAtual.add(valor);
        }
    },
    DEBITO {
        @Override
        public BigDecimal aplicar(BigDecimal saldoAtual, BigDecimal valor) {
            return saldoAtual.subtract(valor);
        }
    };

    public abstract BigDecimal aplicar(BigDecimal saldoAtual, BigDecimal valor);
}
