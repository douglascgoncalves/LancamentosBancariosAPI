package br.com.desafio.lancamentosbancariosapi.controller.exception;

import br.com.desafio.lancamentosbancariosapi.domain.exception.ContaNaoEncontradaException;
import br.com.desafio.lancamentosbancariosapi.domain.exception.SaldoInsuficienteException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(SaldoInsuficienteException.class)
    public ResponseEntity<Map<String, Object>> handleSaldoInsuficiente(SaldoInsuficienteException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", Instant.now());
        body.put("status", HttpStatus.UNPROCESSABLE_ENTITY.value());
        body.put("error", "Regra de Negocio");
        body.put("message", ex.getMessage());

        // 422 é o código ideal para "Entendi o que você quer, mas as regras não permitem"
        return new ResponseEntity<>(body, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    // Aproveite para tratar a conta não encontrada também
    @ExceptionHandler(ContaNaoEncontradaException.class)
    public ResponseEntity<Map<String, Object>> handleContaNaoEncontrada(ContaNaoEncontradaException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", Instant.now());
        body.put("status", HttpStatus.NOT_FOUND.value());
        body.put("error", "Não Encontrado");
        body.put("message", ex.getMessage());

        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", java.time.Instant.now().toString());
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("error", "Erro de Validacao");

        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                fieldErrors.put(error.getField(), error.getDefaultMessage())
        );

        response.put("detalhe", fieldErrors);

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleJsonErrors(HttpMessageNotReadableException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", java.time.Instant.now());
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("error", "Erro na leitura do JSON");

        String message = "Formato de JSON inválido ou valor não aceito.";

        // Verifica se o erro é especificamente de um valor de Enum inválido
        if (ex.getCause() instanceof com.fasterxml.jackson.databind.exc.InvalidFormatException invalidEx) {
            if (invalidEx.getTargetType().isEnum()) {
                message = String.format("Valor '%s' é inválido. Valores aceitos: %s",
                        invalidEx.getValue(),
                        java.util.Arrays.toString(invalidEx.getTargetType().getEnumConstants()));
            }
        }

        response.put("message", message);
        return ResponseEntity.badRequest().body(response);
    }
}