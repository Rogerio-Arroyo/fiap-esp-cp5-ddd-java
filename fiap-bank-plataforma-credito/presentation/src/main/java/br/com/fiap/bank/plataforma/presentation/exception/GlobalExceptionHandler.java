package br.com.fiap.bank.plataforma.presentation.exception;

import br.com.fiap.bank.plataforma.domain.exception.CpfInvalidoException;
import br.com.fiap.bank.plataforma.domain.exception.DecisaoInvalidaException;
import br.com.fiap.bank.plataforma.domain.exception.SolicitacaoCreditoNaoEncontradaException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

/**
 * Tratamento global e centralizado de excecoes da API da Plataforma de Credito
 * (tanto do novo recurso RESTful quanto do adaptador legado), traduzindo
 * excecoes de dominio e de validacao em respostas HTTP padronizadas.
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

    @ExceptionHandler(SolicitacaoCreditoNaoEncontradaException.class)
    public ResponseEntity<ApiErrorResponse> handleNaoEncontrado(SolicitacaoCreditoNaoEncontradaException ex, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiErrorResponse.of(
                HttpStatus.NOT_FOUND.value(), "Recurso Nao Encontrado", ex.getMessage(), request.getRequestURI()));
    }

    @ExceptionHandler({CpfInvalidoException.class, DecisaoInvalidaException.class, IllegalArgumentException.class})
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
