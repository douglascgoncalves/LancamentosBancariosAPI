package br.com.desafio.lancamentosbancariosapi.domain.exception;

public class ContaNaoEncontradaException extends RuntimeException {

    public ContaNaoEncontradaException(String numeroConta) {
        super("Conta não encontrada para o número: " + numeroConta);
    }
}
