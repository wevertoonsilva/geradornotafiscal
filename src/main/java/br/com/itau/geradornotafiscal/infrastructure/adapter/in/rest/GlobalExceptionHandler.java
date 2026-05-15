package br.com.itau.geradornotafiscal.infrastructure.adapter.in.rest;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.net.URI;
import java.util.Arrays;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidationException(MethodArgumentNotValidException ex) {
        String errors = ex.getBindingResult().getAllErrors().stream()
                .map(error -> {
                    if (error instanceof org.springframework.validation.FieldError fieldError) {
                        return fieldError.getField() + ": " + fieldError.getDefaultMessage();
                    }
                    return error.getObjectName() + ": " + error.getDefaultMessage();
                })
                .collect(Collectors.joining(", "));
        
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Falha na validação dos campos");
        problemDetail.setTitle("Requisição Inválida");
        problemDetail.setType(URI.create("https://api.geradornf.com/errors/invalid-request"));
        problemDetail.setProperty("errors", errors);
        return problemDetail;
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ProblemDetail handleMessageNotReadableException(HttpMessageNotReadableException ex) {
        ProblemDetail problemDetail = invalidRequestProblemDetail("Payload JSON inválido ou incompatível com o contrato");
        problemDetail.setProperty("errors", resolveJsonReadError(ex));
        return problemDetail;
    }

    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
    public ProblemDetail handleBusinessException(RuntimeException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        problemDetail.setTitle("Erro de Negócio");
        problemDetail.setType(URI.create("https://api.geradornf.com/errors/business-error"));
        return problemDetail;
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGenericException(Exception ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, "Ocorreu um erro interno inesperado.");
        problemDetail.setTitle("Erro Interno do Servidor");
        problemDetail.setType(URI.create("https://api.geradornf.com/errors/internal-server-error"));
        // Stack trace não é exposto
        return problemDetail;
    }

    private ProblemDetail invalidRequestProblemDetail(String detail) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, detail);
        problemDetail.setTitle("Requisição Inválida");
        problemDetail.setType(URI.create("https://api.geradornf.com/errors/invalid-request"));
        return problemDetail;
    }

    private String resolveJsonReadError(HttpMessageNotReadableException ex) {
        Throwable cause = ex.getMostSpecificCause();
        InvalidFormatException invalidFormatException = findCause(ex, InvalidFormatException.class);
        if (invalidFormatException != null) {
            String field = invalidFormatException.getPath().stream()
                    .map(JsonMappingException.Reference::getFieldName)
                    .filter(java.util.Objects::nonNull)
                    .collect(Collectors.joining("."));
            String value = String.valueOf(invalidFormatException.getValue());
            Class<?> targetType = invalidFormatException.getTargetType();

            if (targetType.isEnum()) {
                String acceptedValues = Arrays.stream(targetType.getEnumConstants())
                        .map(Object::toString)
                        .collect(Collectors.joining(", "));
                return field + ": valor inválido '" + value + "'. Valores aceitos: " + acceptedValues;
            }

            return field + ": valor inválido '" + value + "' para o tipo " + targetType.getSimpleName();
        }

        JsonMappingException jsonMappingException = findCause(ex, JsonMappingException.class);
        if (jsonMappingException != null && !jsonMappingException.getPath().isEmpty()) {
            String field = jsonMappingException.getPath().stream()
                    .map(JsonMappingException.Reference::getFieldName)
                    .filter(java.util.Objects::nonNull)
                    .collect(Collectors.joining("."));
            return field + ": " + cause.getMessage();
        }

        return cause.getMessage();
    }

    private <T extends Throwable> T findCause(Throwable throwable, Class<T> type) {
        Throwable current = throwable;
        while (current != null) {
            if (type.isInstance(current)) {
                return type.cast(current);
            }
            current = current.getCause();
        }
        return null;
    }
}
