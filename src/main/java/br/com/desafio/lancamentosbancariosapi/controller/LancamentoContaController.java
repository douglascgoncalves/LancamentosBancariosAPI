package br.com.desafio.lancamentosbancariosapi.controller;

import br.com.desafio.lancamentosbancariosapi.dto.SaldoContaResponseDTO;
import br.com.desafio.lancamentosbancariosapi.service.ProcessarLancamentosContaService;
import br.com.desafio.lancamentosbancariosapi.usecase.command.ProcessarLancamentosContaCommand;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/contas")
public class LancamentoContaController {
    private static final Logger logger = LoggerFactory.getLogger(LancamentoContaController.class);

    private final ProcessarLancamentosContaService service;

    public LancamentoContaController(ProcessarLancamentosContaService service) {
        this.service = service;
    }

    /**
     * Caso de uso: realizar lancamentos (debito/credito) em uma conta
     * OBS: Permite multiplos lancamentos na mesma requisicao
     * Retorno 204 - Mais performatico pois tem menos dados trafegando na rede
     */
    @PostMapping("/lancamentos")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void realizarLancamentos(@RequestBody @Valid ProcessarLancamentosContaCommand lancamentos) {
        logger.info("Recebida requisição POST /lancamentos para conta {}", lancamentos.getNumeroConta());
        service.processarLancamentos(lancamentos);
        logger.info("Lançamentos processados com sucesso para conta {}", lancamentos.getNumeroConta());
    }

    /**
     * Caso de uso: consultar saldo atual da conta
     */
    @GetMapping("/{numeroConta}/saldo")
    public ResponseEntity<SaldoContaResponseDTO> obterSaldo(
            @PathVariable String numeroConta) {
        logger.info("Recebida requisição GET /saldo para conta {}", numeroConta);
        SaldoContaResponseDTO response = service.obterSaldo(numeroConta);
        logger.info("Consulta de saldo processada com sucesso para conta {}", numeroConta);
        return ResponseEntity.ok(response);
    }
}