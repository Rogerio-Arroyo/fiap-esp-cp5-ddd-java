package br.com.fiap.bank.core.presentation.exception;

import br.com.fiap.bank.core.domain.exception.ClienteNaoEncontradoException;
import br.com.fiap.bank.core.domain.exception.ContaBancariaNaoEncontradaException;
import br.com.fiap.bank.core.domain.exception.ContaInativaException;
import br.com.fiap.bank.core.domain.exception.CpfInvalidoException;
import br.com.fiap.bank.core.domain.exception.CpfJaCadastradoException;
import br.com.fiap.bank.core.domain.exception.EmailInvalidoException;
import br.com.fiap.bank.core.domain.exception.SaldoInsuficienteException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

/**
 * Tratamento global e centralizado de excecoes da API RESTful do Core Bancario,
 * traduzindo excecoes de dominio e de validacao em respostas HTTP padronizadas e semanticamente corretas.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidacao(MethodArgumentNotValidException ex, HttpServletRequest request) {
        List<String> detalhes = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .toList();
        return ResponseEntity.badRequest().body(ApiErrorResponse.of(
                HttpStatus.BAD_REQUEST.value(), "Requisicao Invalida",
                "Um ou mais campos nao atendem as regras de validacao.", request.getRequestURI(), detalhes));
    }

    @ExceptionHandler({ClienteNaoEncontradoException.class, ContaBancariaNaoEncontradaException.class})
    public ResponseEntity<ApiErrorResponse> handleNaoEncontrado(RuntimeException ex, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiErrorResponse.of(
                HttpStatus.NOT_FOUND.value(), "Recurso Nao Encontrado", ex.getMessage(), request.getRequestURI()));
    }

    @ExceptionHandler(CpfJaCadastradoException.class)
    public ResponseEntity<ApiErrorResponse> handleConflito(CpfJaCadastradoException ex, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ApiErrorResponse.of(
                HttpStatus.CONFLICT.value(), "Conflito de Dados", ex.getMessage(), request.getRequestURI()));
    }

    @ExceptionHandler({SaldoInsuficienteException.class, ContaInativaException.class})
    public ResponseEntity<ApiErrorResponse> handleRegraDeNegocio(RuntimeException ex, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ApiErrorResponse.of(
                HttpStatus.CONFLICT.value(), "Violacao de Regra de Negocio", ex.getMessage(), request.getRequestURI()));
    }

    @ExceptionHandler({CpfInvalidoException.class, EmailInvalidoException.class, IllegalArgumentException.class})
    public ResponseEntity<ApiErrorResponse> handleRequisicaoInvalida(RuntimeException ex, HttpServletRequest request) {
        return ResponseEntity.badRequest().body(ApiErrorResponse.of(
                HttpStatus.BAD_REQUEST.value(), "Requisicao Invalida", ex.getMessage(), request.getRequestURI()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGenerico(Exception ex, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiErrorResponse.of(
                HttpStatus.INTERNAL_SERVER_ERROR.value(), "Erro Interno do Servidor",
                "Ocorreu um erro inesperado ao processar a requisicao.", request.getRequestURI()));
    }
}
