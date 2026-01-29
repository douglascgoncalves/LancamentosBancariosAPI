package br.com.desafio.lancamentosbancariosapi.domain.model;

import br.com.desafio.lancamentosbancariosapi.domain.exception.SaldoInsuficienteException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ContaTest {

    @Test
    @DisplayName("Deve aumentar o saldo quando for um lançamento de crédito")
    void deveAumentarSaldoNoCredito() {
        Conta conta = new Conta("12345", new BigDecimal("100.00"));
        Lancamento lancamento = new Lancamento(new BigDecimal("50.00"), TipoLancamento.CREDITO);

        conta.aplicarLancamento(lancamento);

        assertThat(conta.getSaldo()).isEqualByComparingTo("150.00");
    }

    @Test
    @DisplayName("Deve diminuir o saldo quando for um lançamento de débito com saldo suficiente")
    void deveDiminuirSaldoNoDebito() {
        Conta conta = new Conta("12345", new BigDecimal("100.00"));
        Lancamento lancamento = new Lancamento(new BigDecimal("30.00"), TipoLancamento.DEBITO);

        conta.aplicarLancamento(lancamento);

        assertThat(conta.getSaldo()).isEqualByComparingTo("70.00");
    }

    @Test
    @DisplayName("Deve lançar exceção quando débito for maior que o saldo atual")
    void deveLancarExcecaoSaldoInsuficiente() {
        Conta conta = new Conta("12345", new BigDecimal("10.00"));
        Lancamento lancamento = new Lancamento(new BigDecimal("10.01"), TipoLancamento.DEBITO);

        assertThrows(SaldoInsuficienteException.class, () -> {
            conta.aplicarLancamento(lancamento);
        });
    }
}