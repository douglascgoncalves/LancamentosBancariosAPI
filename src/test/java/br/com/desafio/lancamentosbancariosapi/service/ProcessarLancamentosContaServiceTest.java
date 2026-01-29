package br.com.desafio.lancamentosbancariosapi.service;

import br.com.desafio.lancamentosbancariosapi.domain.model.Conta;
import br.com.desafio.lancamentosbancariosapi.domain.model.TipoLancamento;
import br.com.desafio.lancamentosbancariosapi.domain.repository.ContaRepository;
import br.com.desafio.lancamentosbancariosapi.usecase.command.LancamentoCommand;
import br.com.desafio.lancamentosbancariosapi.usecase.command.ProcessarLancamentosContaCommand;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProcessarLancamentosContaServiceTest {

    @Mock
    private ContaRepository contaRepository;

    @InjectMocks
    private ProcessarLancamentosContaService service;

    @Test
    @DisplayName("Deve processar múltiplos lançamentos com sucesso")
    void deveProcessarLancamentosComSucesso() {
        String numeroConta = "12345";
        Conta conta = new Conta(numeroConta, new BigDecimal("100.00"));

        LancamentoCommand l1 = new LancamentoCommand(TipoLancamento.CREDITO, new BigDecimal("50.00"), "Depósito");
        LancamentoCommand l2 = new LancamentoCommand(TipoLancamento.DEBITO, new BigDecimal("20.00"), "Saque");

        ProcessarLancamentosContaCommand command = new ProcessarLancamentosContaCommand(numeroConta,List.of(l1, l2));
        when(contaRepository.findByNumeroConta(numeroConta)).thenReturn(Optional.of(conta));

        // Act (Execução)
        service.processarLancamentos(command);

        // Assert (Verificação)
        verify(contaRepository, times(1)).save(any(Conta.class));
    }
}