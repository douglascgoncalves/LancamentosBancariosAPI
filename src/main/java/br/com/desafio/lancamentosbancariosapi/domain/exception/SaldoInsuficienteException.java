package br.com.desafio.lancamentosbancariosapi.domain.exception;

public class SaldoInsuficienteException extends RuntimeException {

    public SaldoInsuficienteException(String numeroConta) {
        super("Saldo insuficiente para realizar débito na conta " + numeroConta);
    }
}
