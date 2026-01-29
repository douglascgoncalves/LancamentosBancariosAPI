package br.com.desafio.lancamentosbancariosapi.usecase.command;

import br.com.desafio.lancamentosbancariosapi.domain.model.TipoLancamento;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record LancamentoCommand(
        @NotNull(message = "Tipo do lançamento é obrigatorio")
        TipoLancamento tipo,

        @NotNull(message = "O valor é obrigatorio")
        @Positive(message = "O valor do lancamento deve ser maior que zero")
        BigDecimal valor,

        String descricao
) {}
