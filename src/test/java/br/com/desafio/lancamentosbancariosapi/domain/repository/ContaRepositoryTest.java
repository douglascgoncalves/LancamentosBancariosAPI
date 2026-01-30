package br.com.desafio.lancamentosbancariosapi.domain.repository;

import br.com.desafio.lancamentosbancariosapi.domain.model.Conta;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;

@DataJpaTest
class ContaRepositoryTest {

    @Autowired
    private ContaRepository repository;

    @Test
    void deveBuscarContaPorNumero() {
        Conta conta = new Conta("777", BigDecimal.TEN);
        repository.save(conta);

        var encontrada = repository.findByNumeroConta("777");

        assertThat(encontrada).isPresent();
        assertThat(encontrada.get().getNumeroConta()).isEqualTo("777");
    }
}