package br.com.desafio.lancamentosbancariosapi.service;

import br.com.desafio.lancamentosbancariosapi.domain.exception.ContaNaoEncontradaException;
import br.com.desafio.lancamentosbancariosapi.domain.model.Conta;
import br.com.desafio.lancamentosbancariosapi.domain.model.Lancamento;
import br.com.desafio.lancamentosbancariosapi.domain.repository.ContaRepository;
import br.com.desafio.lancamentosbancariosapi.dto.SaldoContaResponseDTO;
import br.com.desafio.lancamentosbancariosapi.usecase.command.ProcessarLancamentosContaCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class ProcessarLancamentosContaService {

    private static final Logger logger =
            LoggerFactory.getLogger(ProcessarLancamentosContaService.class);

    private final ContaRepository contaRepository;

    public ProcessarLancamentosContaService(ContaRepository contaRepository) {
        this.contaRepository = contaRepository;
    }

    /**
     * Caso de uso de escrita
     * Processa uma lista de lançamentos para uma conta
     */
    @Transactional
    public void processarLancamentos(ProcessarLancamentosContaCommand command) {
        logger.info("Iniciando processamento de {} lançamentos para conta {}", command.getLancamentos().size(), command.getNumeroConta());

        //Busca a conta (idealmente com Lock Pessimista no Repository para Thread-Safety)
        Conta conta = contaRepository.findByNumeroConta(command.getNumeroConta())
                .orElseThrow(() -> new ContaNaoEncontradaException(command.getNumeroConta()));

        //Transforma os DTOs em objetos de domínio e aplica um a um
        command.getLancamentos().forEach(dto -> {
            Lancamento lancamento = new Lancamento(dto.valor(), dto.tipo());
            conta.aplicarLancamento(lancamento);
        });

        //Salva a conta com o novo saldo e versão atualizada
        contaRepository.save(conta);

        logger.info("Lançamentos processados com sucesso. Novo saldo da conta {}: {}", command.getNumeroConta(), conta.getSaldo());
    }

    /**
     * Caso de uso de leitura
     * Consulta o saldo atual da conta
     */
    @Transactional(readOnly = true)
    public SaldoContaResponseDTO obterSaldo(String numeroConta) {
        logger.info("Consultando saldo da conta {}", numeroConta);
        Conta conta = contaRepository.findByNumeroConta(numeroConta)
                .orElseThrow(() -> new ContaNaoEncontradaException(numeroConta));
        return new SaldoContaResponseDTO(conta.getNumeroConta(), conta.getSaldo());
    }
}
