package br.com.desafio.lancamentosbancariosapi.usecase.command;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Objects;

@Getter
@Setter
public final class ProcessarLancamentosContaCommand {

    @NotBlank
    private final String numeroConta;

    @NotEmpty
    @Valid
    private final List<LancamentoCommand> lancamentos;

    public ProcessarLancamentosContaCommand(String numeroConta, List<LancamentoCommand> lancamentos){
        this.numeroConta = Objects.requireNonNull(numeroConta, "Numero da conta é obrigatorio");
        this.lancamentos = List.copyOf(Objects.requireNonNull(lancamentos, "Lista de lançamentos é obrigatoria"));
    }
}
