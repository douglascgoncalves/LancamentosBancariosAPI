package br.com.desafio.lancamentosbancariosapi.integration;

import br.com.desafio.lancamentosbancariosapi.domain.model.Conta;
import br.com.desafio.lancamentosbancariosapi.domain.repository.ContaRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import java.math.BigDecimal;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class LockOtimistaTest {

    @Autowired
    private ContaRepository repository;

    @Test
    void deveLancarExcecaoQuandoDuasThreadsTentaremAtualizarMesmaVersao() throws InterruptedException {
        // 1. Preparamos uma conta no banco
        Conta conta = new Conta("999", new BigDecimal("1000.00"));
        repository.save(conta);

        ExecutorService executor = Executors.newFixedThreadPool(2);
        AtomicInteger errosDeConcorrencia = new AtomicInteger();

        // 2. Duas tarefas tentam ler e atualizar a conta simultaneamente
        Runnable tarefa = () -> {
            try {
                Conta c = repository.findByNumeroConta("999").get();
                c.setSaldo(c.getSaldo().add(new BigDecimal("10.00")));
                repository.save(c);
            } catch (ObjectOptimisticLockingFailureException e) {
                errosDeConcorrencia.incrementAndGet();
            }
        };

        executor.submit(tarefa);
        executor.submit(tarefa);

        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);

        assertThat(errosDeConcorrencia.get()).isPositive();
    }
}