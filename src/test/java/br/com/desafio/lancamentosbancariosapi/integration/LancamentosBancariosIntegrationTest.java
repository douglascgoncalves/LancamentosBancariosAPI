package br.com.desafio.lancamentosbancariosapi.integration;


import br.com.desafio.lancamentosbancariosapi.domain.model.Conta;
import br.com.desafio.lancamentosbancariosapi.domain.model.TipoLancamento;
import br.com.desafio.lancamentosbancariosapi.domain.repository.ContaRepository;
import br.com.desafio.lancamentosbancariosapi.usecase.command.LancamentoCommand;
import br.com.desafio.lancamentosbancariosapi.usecase.command.ProcessarLancamentosContaCommand;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class LancamentosBancariosIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ContaRepository contaRepository;

    private final String NUMERO_CONTA = "54321";

    @BeforeEach
    void setUp() {
        Conta conta = new Conta(NUMERO_CONTA, new BigDecimal("100.00"));
        contaRepository.save(conta);
    }

    @Test
    @DisplayName("Caminho Feliz: Deve processar crédito e débito e atualizar saldo")
    void deveProcessarLancamentosComSucesso() throws Exception {
        var l1 = new LancamentoCommand(TipoLancamento.CREDITO, new BigDecimal("50.00"), "Depósito");
        var l2 = new LancamentoCommand(TipoLancamento.DEBITO, new BigDecimal("30.00"), "Saque");
        var command = new ProcessarLancamentosContaCommand(NUMERO_CONTA, List.of(l1, l2));

        mockMvc.perform(post("/v1/contas/" + NUMERO_CONTA + "/lancamentos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isOk());

        // Valida se o saldo no banco virou 120.00 (100 + 50 - 30)
        Conta contaAtualizada = contaRepository.findByNumeroConta(NUMERO_CONTA).get();
        assertThat(contaAtualizada.getSaldo()).isEqualByComparingTo("120.00");
    }

    @Test
    @DisplayName("Erro de Negócio: Deve retornar 422 quando saldo for insuficiente")
    void deveRetornarErroSaldoInsuficiente() throws Exception {
        var debitoExcessivo = new LancamentoCommand(TipoLancamento.DEBITO, new BigDecimal("200.00"), "Saque alto");
        var command = new ProcessarLancamentosContaCommand(NUMERO_CONTA, List.of(debitoExcessivo));

        mockMvc.perform(post("/v1/contas/" + NUMERO_CONTA + "/lancamentos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.message").value("Saldo insuficiente para realizar débito na conta " + NUMERO_CONTA));
    }

    @Test
    @DisplayName("Erro de Validação: Deve retornar 400 quando valor for negativo")
    void deveRetornarErroValidacaoValorNegativo() throws Exception {
        var lancamentoInvalido = new LancamentoCommand(TipoLancamento.CREDITO, new BigDecimal("-10.00"), "Erro");
        var command = new ProcessarLancamentosContaCommand(NUMERO_CONTA, List.of(lancamentoInvalido));

        mockMvc.perform(post("/v1/contas/" + NUMERO_CONTA + "/lancamentos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Erro de Validação"));
    }

    @Test
    @DisplayName("Erro de Recurso: Deve retornar 404 quando conta não existir")
    void deveRetornarErroContaInexistente() throws Exception {
        var command = new ProcessarLancamentosContaCommand("99999", List.of());

        mockMvc.perform(post("/v1/contas/99999/lancamentos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Erro de JSON: Deve retornar 400 quando o Enum for digitado errado")
    void deveRetornarErroJsonMalformado() throws Exception {
        String jsonErrado = """
            {
                "numeroConta": "54321",
                "lancamentos": [{ "valor": 10.0, "tipo": "CREDIIITO" }]
            }
            """;

        mockMvc.perform(post("/v1/contas/54321/lancamentos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonErrado))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }
}