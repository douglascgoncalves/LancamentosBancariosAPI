package br.com.desafio.lancamentosbancariosapi.dto;

import java.math.BigDecimal;

/**
 * DTO para representar o saldo da conta na resposta da API.
 * Records são imutáveis e ideais para transferência de dados.
 */
public record SaldoContaResponseDTO(
        String numeroConta,
        BigDecimal saldo
) {}
