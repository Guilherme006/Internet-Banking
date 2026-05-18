package com.banco.pagamento.adapters.inbound.rest.handler;

import com.banco.pagamento.application.domain.exception.BoletoJaPagoException;
import com.banco.pagamento.application.domain.exception.BoletoNaoEncontradoException;
import com.banco.pagamento.application.domain.exception.ContaNaoEncontradaException;
import com.banco.pagamento.application.domain.exception.CredenciaisInvalidasException;
import com.banco.pagamento.application.domain.exception.SaldoInsuficienteException;
import com.banco.pagamento.application.domain.exception.UsuarioJaCadastradoException;
import com.banco.pagamento.application.domain.exception.UsuarioNaoEncontradoException;
import com.banco.pagamento.adapters.outbound.external.exception.ServicoExternoException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ContaNaoEncontradaException.class)
    public ProblemDetail handleContaNaoEncontrada(ContaNaoEncontradaException ex) {
        log.warn("Conta não encontrada: {}", ex.getMessage());
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(BoletoNaoEncontradoException.class)
    public ProblemDetail handleBoletoNaoEncontrado(BoletoNaoEncontradoException ex) {
        log.warn("Boleto não encontrado: {}", ex.getMessage());
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(SaldoInsuficienteException.class)
    public ProblemDetail handleSaldoInsuficiente(SaldoInsuficienteException ex) {
        log.warn("Saldo insuficiente: {}", ex.getMessage());
        return ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage());
    }

    @ExceptionHandler(BoletoJaPagoException.class)
    public ProblemDetail handleBoletoJaPago(BoletoJaPagoException ex) {
        log.warn("Boleto já pago: {}", ex.getMessage());
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(CredenciaisInvalidasException.class)
    public ProblemDetail handleCredenciaisInvalidas(CredenciaisInvalidasException ex) {
        log.warn("Tentativa de autenticação inválida");
        return ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, ex.getMessage());
    }

    @ExceptionHandler(UsuarioJaCadastradoException.class)
    public ProblemDetail handleUsuarioJaCadastrado(UsuarioJaCadastradoException ex) {
        log.warn("Cadastro duplicado: {}", ex.getMessage());
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(UsuarioNaoEncontradoException.class)
    public ProblemDetail handleUsuarioNaoEncontrado(UsuarioNaoEncontradoException ex) {
        log.warn("Usuário autenticado não encontrado");
        return ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, ex.getMessage());
    }

    @ExceptionHandler(ServicoExternoException.class)
    public ProblemDetail handleServicoExterno(ServicoExternoException ex) {
        log.error("Serviço externo indisponível: {}", ex.getMessage());
        return ProblemDetail.forStatusAndDetail(HttpStatus.SERVICE_UNAVAILABLE,
            "Serviço de validação temporariamente indisponível. Tente novamente em instantes.");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidacao(MethodArgumentNotValidException ex) {
        String erros = ex.getBindingResult().getFieldErrors().stream()
            .map(e -> e.getField() + ": " + e.getDefaultMessage())
            .collect(Collectors.joining("; "));
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, erros);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ProblemDetail handleValidacaoParametros(ConstraintViolationException ex) {
        String erros = ex.getConstraintViolations().stream()
            .map(e -> e.getPropertyPath() + ": " + e.getMessage())
            .collect(Collectors.joining("; "));
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, erros);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ProblemDetail handleRecursoNaoEncontrado(NoResourceFoundException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, "Recurso não encontrado.");
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGenerico(Exception ex) {
        log.error("Erro inesperado", ex);
        return ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR,
            "Erro interno. Por favor, contate o suporte.");
    }
}
