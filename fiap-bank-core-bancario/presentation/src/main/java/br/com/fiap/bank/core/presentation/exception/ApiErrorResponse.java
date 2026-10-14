package br.com.fiap.bank.core.presentation.exception;

import java.time.LocalDateTime;
import java.util.List;

/** Representacao padronizada do corpo de resposta de erro, devolvida pelo GlobalExceptionHandler. */
public record ApiErrorResponse(
        LocalDateTime timestamp,
        int status,
        String error,
        String message,
        String path,
        List<String> detalhes
) {
    public static ApiErrorResponse of(int status, String error, String message, String path) {
        return new ApiErrorResponse(LocalDateTime.now(), status, error, message, path, List.of());
    }

    public static ApiErrorResponse of(int status, String error, String message, String path, List<String> detalhes) {
        return new ApiErrorResponse(LocalDateTime.now(), status, error, message, path, detalhes);
    }
}
